import java.io.*;
import java.math.BigDecimal;
import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;
import java.sql.*;
import java.util.Arrays;

import jakarta.servlet.http.*;
import jakarta.servlet.annotation.*;

@WebServlet(name = "helloServlet", value = "/hello-servlet")
public class HelloServlet extends HttpServlet {
    private String[] message;
    DBsupport connect;
    static private final String driver = "org.postgresql.Driver";
    static private final String DBurl = "jdbc:postgresql://localhost:5432/clever_bank";
    static private final String username = "postgres";
    static private final String password = "SeredinovA13";
    static private final byte[] salt = {-123, 98, 0, 89, 29, 109, 35, 127, 111, 87, -90, 67, 43, -50, 78, -12};
    static private final String[] clientStates = {"сс", "ус", "вп", "сл", "сп"};
    static private final String[] adminStates = {"дб", "уб", "пб"};

    public void init() {
        connect = new DBsupport(driver, DBurl, username, password);
        try {
            connect.attachBD();
        } catch (ClassNotFoundException e) {
            System.out.println("No DB driver error!");
            throw new RuntimeException(e);
        } catch (SQLException e) {
            System.out.println("attachDB error!");
            throw new RuntimeException(e);
        }
    }

    public void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
        response.setContentType("text/html");

        // Hello
        PrintWriter out = response.getWriter();
        out.println("<html><body>");
        out.println("<h1>GET_OK</h1>");
        out.println("</body></html>");
        System.out.println("connection");
    }

    public void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException {
        try {
            System.out.println("connection");
            String sessionId = request.getSession().getId();
            PrintWriter writer = response.getWriter();
            message = request.getParameter("request").split(" ");
            String[] rez = connect.getSessionData(sessionId);
            if (rez[1].isEmpty()) {
                connect.addSession(sessionId);
                rez = connect.getSessionData(sessionId);
            }
            if (message[0].equals("в")) {
                connect.removeSession(sessionId);
            }
            if (message[0].equals("н")) {
                if (Arrays.asList(clientStates).contains(rez[1])) {
                    connect.updateSession(sessionId, "л+");
                    helloClient(sessionId,writer);
                } else if (Arrays.asList(adminStates).contains(rez[1])) {
                    connect.updateSession(sessionId, "л++");
                    helloAdmin(sessionId,writer);
                } else {
                    connect.updateSession(sessionId, "н");
                    writer.println("Требуемая информация во всех последующих запросах вводится через пробелы.");
                    writer.println("Введите л для входа в аккаунт.");
                    writer.println("Введите нп для создания аккаунта.");
                    writer.println("Введите в для выхода из приложения.");
                }
            } else {
                switch (rez[1]) {
                    //Функции авторизации
                    case "н": {
                        switch (message[0]) {
                            case "л":
                                System.out.println(" л ");
                                writer.println("Введите логин и пароль через пробел.");
                                connect.updateSession(sessionId, "л");
                                break;
                            case "нп":
                                writer.println("Введите имя, фамилию, отчество, логин и пароль через пробел");
                                connect.updateSession(sessionId, "нп");
                                break;
                            default:
                                writer.println("Выберите из предложеных опций.");
                        }
                        break;
                    }


                    case "л": {
                        Hash hash = new Hash(salt);
                        if (connect.authorisation(sessionId,message[0], hash.createHash(message[1]))) {
                            writer.println("Вы вошли как " + message[0]+".");
                            if (connect.isAdmin(message[0])) {
                                helloAdmin(sessionId, writer);
                            } else {
                                helloClient(sessionId, writer);
                            }
                        } else {
                            wrongData(writer);
                        }
                        break;
                    }


                    case "нп": {
                        if (connect.isLoginExist(message[3])) {
                            writer.println("Такой логин уже существует.");
                        } else {
                            Hash hash = new Hash(salt);
                            if (connect.addClient(message[0], message[1], message[2], message[3], hash.createHash(message[4]))) {
                                connect.updateSession(sessionId, message[3], "л+");
                                writer.println("Новый аккаунт создан. Вы вошли как " + message[3]+".");
                            } else {
                                wrongData(writer);
                            }
                        }
                        break;
                    }
                    //Возможности клиента
                    case "л+": {
                        switch (message[0]) {
                            case "сс": {
                                writer.println("Введите название банка, в котором желаете создать счёт, и его валюту(трёхбуквенное сокращение).");
                                writer.println("Доступные банки:");
                                writer.println(connect.getBanks());
                                writer.println("Доступные валюты:");
                                writer.println(connect.getCurrencyTypes());
                                connect.updateSession(sessionId, "сс");
                                break;
                            }
                            case "ус": {
                                writer.println("Введите номер удаляемого счёта.");
                                writer.println("Ваши счета:");
                                connect.getBankAccounts(sessionId);
                                connect.updateSession(sessionId, "ус");
                                break;
                            }
                            case "вп": {
                                writer.println("Введите номер счета источника, номер счета получателя и сумму.");
                                writer.println("Доступные счета:");
                                System.out.println(connect.getBankAccounts(sessionId));
                                connect.updateSession(sessionId, "вп");
                                break;
                            }
                            case "сл": {
                                writer.println("Введите новый логин.");
                                connect.updateSession(sessionId, "сл");
                                break;
                            }
                            case "сп": {
                                writer.println("Введите новый пароль.");
                                connect.updateSession(sessionId, "сп");
                                break;
                            }
                            default:
                                writer.println("Выберите из предложеных опций.");
                        }
                        break;
                    }
                    case "сс": {
                        String accountNumber = connect.addBankAccount(sessionId,message[0], message[1]);
                        if (accountNumber.isEmpty()) {
                            wrongData(writer);
                        } else {
                            writer.println("Вы создали счет с номером: " + accountNumber);
                            helloClient(sessionId, writer);
                        }
                        break;
                    }
                    case "ус": {
                        if (connect.removeBankAccount(message[0])) {
                            writer.println("Счёт успешно удалён.");
                            helloClient(sessionId, writer);
                        } else {
                            wrongData(writer);
                        }
                        break;
                    }
                    case "вп": {
                        if (connect.transaction(message[0], message[1], new BigDecimal(message[2]))) {
                            writer.println("Перевод выполнен успешно.");
                            helloClient(sessionId, writer);
                        } else {
                            wrongData(writer);
                        }
                        break;
                    }
                    case "сл": {
                        String login = connect.getSessionData(sessionId)[0];
                        if(connect.changeClientLogin(login, message[0])){
                            helloClient(sessionId, writer);
                        }
                        wrongData(writer);
                        break;
                    }
                    case "сп": {
                        String login = connect.getSessionData(sessionId)[0];
                        Hash hash = new Hash(salt);
                        if (connect.changeClientPassword(login, hash.createHash(message[0]))) {
                            writer.println("Пароль успешно изменён.");
                            helloClient(sessionId, writer);
                        } else {
                            wrongData(writer);
                        }
                        break;
                    }
                    //Возможности администратора
                    case "л++": {
                        switch (message[0]) {
                            case "дб": {
                                writer.println("Введите название нового банка.");
                                connect.updateSession(sessionId, "дб");
                                break;
                            }
                            case "уб": {
                                writer.println("Введите название банка для удаления.");
                                writer.println("Доступные банки:");
                                writer.println(connect.getBanks());
                                connect.updateSession(sessionId, "уб");
                                break;
                            }
                            case "пб": {
                                writer.println("Введите старое и новое названия банка через пробел.");
                                writer.println("Доступные банки:");
                                writer.println(connect.getBanks());
                                connect.updateSession(sessionId, "пб");
                                break;
                            }
                            case"к":{
                                helloClient(sessionId,writer);
                                break;
                            }
                            default:
                                writer.println("Выберите из предложеных опций.");
                        }
                        break;
                    }


                    case "дб": {
                        if (connect.addBank(message[0])) {
                            writer.println("Банк создан успешно.");
                            helloAdmin(sessionId, writer);
                        } else {
                            wrongData(writer);
                        }
                        break;
                    }
                    case "уб": {
                        if (connect.removeBank(message[0])) {
                            writer.println("Банк удалён успешно.");
                            helloAdmin(sessionId, writer);
                        } else {
                            wrongData(writer);
                        }
                        break;
                    }
                    case "пб": {
                        if (connect.renameBank(message[0], message[1])) {
                            writer.println("Банк переименован успешно.");
                            helloAdmin(sessionId, writer);
                        } else {
                            wrongData(writer);
                        }
                        break;
                    }

                    default: {
                        System.out.println("Произошла внутренняя ошибка, попробуйте перезапустить приложение.");
                    }
                }

            }
        } catch (SQLException e) {
            System.out.println(e.getMessage());
            throw new RuntimeException(e);
        } catch (NoSuchAlgorithmException | InvalidKeySpecException e) {
            throw new RuntimeException(e);
        }
    }


    public void destroy() {
        try {
            connect.detachDB();
        } catch (SQLException e) {
            System.out.println("detachDB error!");
        }
    }

    //
    private void helloClient(String sessionId, PrintWriter writer) throws SQLException {
        writer.println("Введите сс чтобы создать счёт.");
        writer.println("Введите ус чтобы удалить счёт.");
        writer.println("Введите вп чтобы выполнить перевод");
        connect.updateSession(sessionId,  "л+");
    }

    private void helloAdmin(String sessionId, PrintWriter writer) throws SQLException {
        writer.println("Приветствую администратор!");
        writer.println("Введите дб чтобы добавить банк.");
        writer.println("Введите уб чтобы удалить банк.");
        writer.println("Введите пб чтобы добавить банк.");
        writer.println("Введите к чтобы перейти в режим клиента.");
        writer.println("(Для возвращения возможностей администратора, придется повторить авторизацию.)");
        connect.updateSession(sessionId,  "л++");
    }

    private void wrongData(PrintWriter writer) {
        writer.println("Неверные данные. Попробуйте снова.");
    }
}