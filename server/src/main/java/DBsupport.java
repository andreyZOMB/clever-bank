import java.math.BigDecimal;
import java.sql.*;

public class DBsupport {
    public DBsupport(String driver, String DBurl, String username, String password) {
        this.driver = driver;
        this.DBurl = DBurl;
        this.username = username;
        this.password = password;
    }

    private final String driver;
    private final String DBurl;
    private final String username;
    private final String password;

    Connection DBconnection;
    Statement st;

    public void attachBD() throws ClassNotFoundException, SQLException {
        Class.forName(driver);
        DBconnection = DriverManager.getConnection(DBurl, username, password);
        st = DBconnection.createStatement();
    }

    public void detachDB() throws SQLException {
        st.close();
        DBconnection.close();
    }

    // Session control
    public String[] getSessionData(String sessionId) throws SQLException {
        ResultSet rez = st.executeQuery("SELECT user_id,last_command FROM sessions WHERE session_id = '" + sessionId + "';");
        String[] output = new String[2];
        if (rez.next()) {
            output[0] = rez.getString("user_id");
            output[1] = rez.getString("last_command");
        } else {
            output[0] = "";
            output[1] = "";
        }
        return output;
    }

    public boolean addSession(String sessionId){
        try {
        return (0 < st.executeUpdate("insert into sessions(session_id,user_id,last_command) values ('" + sessionId + "',null,'н');"));
    }catch (SQLException e){
        return false;
    }
    }

    public boolean updateSession(String sessionId, String user_id, String command) {
        try {
        return (0 < st.executeUpdate("UPDATE sessions SET last_command = '" + command + "',user_id = '" + user_id + "',created_in = NOW()  WHERE session_id = '" + sessionId + "';"));
    }catch (SQLException e){
        return false;
    }
    }


    public boolean updateSession(String sessionId, String command) {
        try {
            return (0 < st.executeUpdate("UPDATE sessions SET last_command = '" + command + "' ,created_in = NOW() WHERE session_id = '" + sessionId + "';"));
        }catch (SQLException e){
            return false;
        }
    }

    public boolean removeSession(String sessionId) {
        try {
            return (0 < st.executeUpdate("DELETE FROM sessions WHERE session_id = '" + sessionId + "';"));
        } catch (SQLException e) {
            return false;
        }
    }

    // Authorisation
    public boolean authorisation(String sessionId, String login, String hash) {
        try {
            ResultSet rez = st.executeQuery("SELECT id FROM users WHERE login =  '" + login + "' and hash = '" + hash + "' ;");
            if (rez.next()) {
                st.executeUpdate("UPDATE sessions SET user_id =(select id from users,sessions where users.login = '" + login + "' and session_id ='" + sessionId + "') WHERE session_id = '" + sessionId + "';");
                return true;
            }
            return false;
        } catch (SQLException e) {
            return false;
        }
    }

    public boolean isAdmin(String login) {
        try {
            ResultSet rez = st.executeQuery("SELECT is_admin FROM users WHERE login =  '" + login + "' and is_admin = TRUE ;");
            return rez.next();
        } catch (SQLException e) {
            return false;
        }
    }

    //
    public boolean addClient(String firstName, String lastName, String otchestvo, String login, String hash) throws SQLException {
        return (0 < st.executeUpdate("insert into users(first_name,last_name,otchestvo,login,hash) values ('" + firstName + "','" + lastName + "','" + otchestvo + "','" + login + "','" + hash + "');"));
    }

    public boolean isLoginExist(String login) throws SQLException {
        ResultSet rez = st.executeQuery("SELECT login FROM users WHERE login = '" + login + "';");
        return rez.next();
    }

    public boolean changeClientLogin(String oldLogin, String newLogin) throws SQLException {
        return (0 < st.executeUpdate("UPDATE users SET login = '" + newLogin + "'WHERE login = '" + oldLogin + "';"));
    }

    public boolean changeClientPassword(String login, String newHash) throws SQLException {
        return (0 < st.executeUpdate("UPDATE users SET hash = '" + newHash + "'WHERE login = '" + login + "';"));
    }

    public boolean addBank(String name) throws SQLException {
        try {
            return (0 < st.executeUpdate("insert into banks(name) values ('" + name + "');"));
        } catch (SQLException e) {
            return false;
        }
    }

    public boolean removeBank(String name) throws SQLException {
        try {
            return (0 < st.executeUpdate("DELETE FROM banks WHERE name = '" + name + "';"));
        } catch (SQLException e) {
            return false;
        }
    }

    public boolean renameBank(String oldName, String newName) {
        try {
            return (0 < st.executeUpdate("UPDATE banks SET name = '" + newName + "' WHERE name = '" + oldName + "';"));
        } catch (SQLException e) {
            System.out.println(e.getMessage());
            return false;
        }
    }

    public String addBankAccount(String sessionId, String bank, String currency) throws SQLException {
        ResultSet rez = st.executeQuery("insert into bank_account(client,bank,currency) values ((SELECT user_id FROM sessions WHERE session_id ='" + sessionId + "'),(SELECT id FROM banks WHERE name = 'Clever'),'1') RETURNING account_number;");
        if (rez.next()) {
            return rez.getString("account_number");
        }
        return "";
    }

    public boolean removeBankAccount(String bankAccountNumber) throws SQLException {
        return (0 < st.executeUpdate("DELETE FROM bank_account WHERE account_number = '" + bankAccountNumber + "';"));
    }

    public boolean transaction(String transmitter, String receiver, BigDecimal value) throws SQLException {
        MoneyData transmitterData = getBankAccountData(transmitter);
        MoneyData receiverData = getBankAccountData(receiver);
        if (transmitterData.currency.equals(receiverData.currency) && ((receiverData.value.compareTo(value)) > -1)) {
            st.executeUpdate("UPDATE bank_account SET (value = value+'" + value + "') WHERE account_number = '" + receiver + "';");
            st.executeUpdate("UPDATE bank_account SET (value = value-'" + value + "') WHERE account_number = '" + transmitter + "';");
            addTransaction(transmitter, receiver, value, receiverData.currency);
            return true;
        }
        return false;
    }

    public boolean removeObsoleteSessions() {
        try {
            return (0 < st.executeUpdate("DELETE FROM sessions where (created_in+INTERVAL '30 minutes') <now();"));
        } catch (SQLException e) {
            return false;
        }
    }

    public boolean checkAddingPercent() {
        try {
            st.executeUpdate("UPDATE bank_account SET capitalisation = (value > 1000);");
        } catch (SQLException e) {
            return false;
        }
        return false;
    }

    public String getBanks() {
        try {
            String out = "";
            ResultSet rez = st.executeQuery("SELECT name FROM banks;");
            while (rez.next()) {
                out += rez.getString("name");
                out += "\n";
            }
            if (out.isEmpty()) {
                return "Банки не найдены.";
            }
            return out;
        } catch (SQLException e) {
            return "Банки не найдены.";
        }
    }

    public String getCurrencyTypes() {
        try {
            String out = "";
            ResultSet rez = st.executeQuery("SELECT name FROM currency;");
            while (rez.next()) {
                out += rez.getString("name");
                out += "\n";
            }
            if (out.isEmpty()) {
                return "Валюты не найдены.";
            }
            return out;
        } catch (SQLException e) {
            return "Валюты не найдены.";
        }
    }

    public String getBankAccounts(String sessionId) {
        try {
            String out = "";
            ResultSet rez = st.executeQuery("SELECT account_number FROM bank_account,sessions where session_id ='" + sessionId + "' and sessions.user_id = bank_account.client;");
            while (rez.next()) {
                out += rez.getString("account_number");
                out += "\n";
            }
            if (out.isEmpty()) {
                return "У вас нет открытых счетов.";
            }
            return out;
        } catch (SQLException e) {
            return "Произошла ошибка, попробуйте снова.";
        }
    }

    private MoneyData getBankAccountData(String accountNumber) throws SQLException {
        MoneyData result;
        ResultSet rez = st.executeQuery("SELECT currency,value FROM bank_account WHERE account_number =  '" + accountNumber + "';");
        if (rez.next()) {
            result = new MoneyData(rez.getString("currency"), rez.getBigDecimal("value"));
        } else {
            result = new MoneyData("", BigDecimal.ZERO);
        }
        return result;
    }

    private boolean addTransaction(String transmitter, String receiver, BigDecimal value, String currency) throws
            SQLException {
        ResultSet rez = st.executeQuery("SELECT id FROM bank_account WHERE account_number =  '" + transmitter + "';");
        if (rez.next()) {
            long transmitterId = rez.getLong("id");
            rez = st.executeQuery("SELECT id FROM bank_account WHERE account_number =  '" + receiver + "';");
            if (rez.next()) {
                long receiverId = rez.getLong("id");
                st.executeQuery("insert into transaction(transmitter,receiver,currency,value) values ('" + transmitterId + "','" + receiverId + "','" + currency + "','" + value + "');");
                return true;
            }
        }
        return false;
    }
}
