import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory;

import java.io.File;
import java.io.IOException;
import java.sql.SQLException;
import java.util.Timer;
import java.util.TimerTask;

public class CheckTimer extends TimerTask{
    static DBsupport connect;
    private static final String resoursePath = "/clever-bank/src/main/resources";

    public void run() {

        completeTask();

    }

    private void completeTask() {
        connect.removeObsoleteSessions();
        connect.checkAddingPercent();
        System.out.println("clear");
    }

    public static void main(String args[]){
        ObjectMapper om = new ObjectMapper(new YAMLFactory());
        DBdata data;
        Percent percent;
        try {
            data =  om.readValue(new File(resoursePath, "DBconfig.yml"),DBdata.class);
            percent=om.readValue(new File(resoursePath, "percent.yml"),Percent.class);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        connect = new DBsupport(data.driver, data.DBurl, data.username, data.password);
        try {
            connect.attachBD();
        } catch (ClassNotFoundException e) {
            System.out.println("No DB driver error!");
            throw new RuntimeException(e);
        } catch (SQLException e) {
            System.out.println("attachDB error!");
            throw new RuntimeException(e);
        }
        TimerTask timerTask = new CheckTimer();
        Timer timer = new Timer(true);
        timer.scheduleAtFixedRate(timerTask, 0, 30000);
        System.out.println("TimerTask начал выполнение");
    }

}
