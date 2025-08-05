import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

public class PostgresQueryExample {

    public static void main(String[] args) {
        String host = "top-postgres-pre-flex.postgres.database.azure.com";
        String databaseName = "your_database_name"; // Replace with your database name
        String user = "your_username"; // Replace with your username
        String password = "your_password"; // Replace with your password

        String connectionUrl = "jdbc:postgresql://" + host + ":5432/" + databaseName;

        Connection connection = null;
        Statement statement = null;
        ResultSet resultSet = null;

        try {
            // Register the JDBC driver (optional for modern JDBC, but good practice)
            Class.forName("org.postgresql.Driver");

            // Establish the connection
            System.out.println("Attempting to connect to the database...");
            connection = DriverManager.getConnection(connectionUrl, user, password);
            System.out.println("Connection successful!");

            // Create a statement object
            statement = connection.createStatement();

            // Define your SQL query
            // Replace "your_table_name" and "your_column" with actual values
            String sqlQuery = "SELECT * FROM your_table_name;";

            // Execute the query
            System.out.println("Executing query: " + sqlQuery);
            resultSet = statement.executeQuery(sqlQuery);

            // Process the result set
            System.out.println("Query results:");
            while (resultSet.next()) {
                // Example of getting data from a column named "id" and "name"
                // Replace with your actual column names and data types
                int id = resultSet.getInt("id");
                String name = resultSet.getString("name");
                System.out.println("ID: " + id + ", Name: " + name);
            }

        } catch (ClassNotFoundException e) {
            System.err.println("PostgreSQL JDBC Driver not found in the classpath.");
            e.printStackTrace();
        } catch (SQLException e) {
            System.err.println("Database connection failed!");
            e.printStackTrace();
        } finally {
            // Close the resources in a `finally` block to ensure they are always closed
            try {
                if (resultSet != null) resultSet.close();
                if (statement != null) statement.close();
                if (connection != null) connection.close();
                System.out.println("Resources closed.");
            } catch (SQLException e) {
                System.err.println("Error closing resources: " + e.getMessage());
            }
        }
    }
}
