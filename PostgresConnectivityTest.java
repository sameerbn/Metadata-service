import java.net.InetSocketAddress;
import java.net.Proxy;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.Statement;
import java.sql.SQLException;

public class PostgresConnectivityTest {

    public static void main(String[] args) {
        String host = "top-postgres-pre-flex.postgres.database.azure.com";
        int port = 5432;

        // Your proxy settings from application.yml
        String proxyHost = "socks-proxy.datalakefeeder.dev.azw.gbis.sg-azure.com";
        int proxyPort = 443;

        // PostgreSQL JDBC URL
        // Make sure to use the correct database name and other parameters from your application.yml
        String dbUrl = "jdbc:postgresql://" + host + ":" + port + "/tpspr?sslmode=require";

        // Your database credentials
        String user = "topowner@top-postgres-pre-flex"; // Correct username
        String password = "your_password_here"; // Replace with your actual password

        // Configure JVM to use the SOCKS proxy for this connection
        System.setProperty("socksProxyHost", proxyHost);
        System.setProperty("socksProxyPort", String.valueOf(proxyPort));

        // Use a try-with-resources block to ensure the connection is closed
        try (Connection conn = DriverManager.getConnection(dbUrl, user, password)) {
            System.out.println("Connection successful.");

            // Now, let's insert a value into the scan_targets table
            // You will need to know the exact column names of your table.
            String sql = "INSERT INTO scan_targets (id, target_name, path, is_active) VALUES (1, 'Test Target', '/test/path', TRUE)";

            // Create a statement object
            try (Statement stmt = conn.createStatement()) {
                // Execute the INSERT statement
                int rowsAffected = stmt.executeUpdate(sql);
                System.out.println("Insert successful. " + rowsAffected + " row(s) affected.");
            }

        } catch (SQLException e) {
            System.err.println("Database error: " + e.getMessage());
            e.printStackTrace();
        } finally {
            // It's a good practice to clear the system properties after the test.
            System.clearProperty("socksProxyHost");
            System.clearProperty("socksProxyPort");
        }
    }
}
