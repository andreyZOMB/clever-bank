import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.*;
import java.net.URL;
import java.util.Scanner;

public class Main {
    static String server_url = "";
    static String user_input = "";
    static String[] server_response;

    public static void main(String[] args) throws IOException {
        CookieManager cookieManager = new CookieManager(null, CookiePolicy.ACCEPT_ALL);
        CookieHandler.setDefault(cookieManager);
        Scanner sc1 = new Scanner(System.in);
        System.out.println("Требуемая информация во всех последующих запросах вводится через пробелы.");
        System.out.println("Введите л для входа в аккаунт");
        System.out.println("Введите нп для создания аккаунта");
        System.out.println("Введите в для выхода из приложения");
        while (true) {
            user_input = sc1.nextLine();
            server_response = getRawResponse(server_url, "request=" + user_input);
            int i = 0;
            while ((i <= 100) && (server_response[i]!=null)) {
                System.out.println(server_response[i]);
                i++;
            }
            if (user_input.equals("в")) break;
        }
    }

    static private String[] getRawResponse(String url, String requestBody)
            throws IOException {
        String[] urlString = new String[100];
        HttpURLConnection connection = (HttpURLConnection) new URL(url).openConnection();
        connection.setConnectTimeout(10000);
        connection.setRequestMethod("POST");

        connection.setDoOutput(true);

        try (OutputStreamWriter writer = new OutputStreamWriter(connection.getOutputStream())) {
            writer.write(requestBody);
        }

        if (connection.getResponseCode() != 200) {
            urlString[0] = "connection failed";
            return urlString;
        }

        BufferedReader in = new BufferedReader(
                new InputStreamReader(connection.getInputStream()));

        String current;
        int i = 0;
        while ((current = in.readLine()) != null) {
            urlString[i] = current;
            i++;
        }
        return urlString;
    }
}
