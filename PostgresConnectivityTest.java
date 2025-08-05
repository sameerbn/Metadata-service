import java.net.Socket;

public class PostgresConnectivityTest {
    public static void main(String[] args) {
        String host = "<your-server>.postgres.database.azure.com";
        int port = 5432;

        try (Socket socket = new Socket(host, port)) {
            System.out.println("Connection successful!");
        } catch (Exception e) {
            System.out.println("Connection failed: " + e.getMessage());
        }
    }
}
