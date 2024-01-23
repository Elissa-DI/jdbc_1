import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Scanner;

public class StudentDatabaseApp {

    public static void main(String[] args) {
        try {
            // Load the JDBC driver
            Class.forName("com.mysql.cj.jdbc.Driver");

            // Replace these placeholders with your MySQL database connection details
            String url = "jdbc:mysql://localhost:3306/jdbc_students";
            String username = "root";
            String password = "";

            // Establish the connection
            Connection connection = DriverManager.getConnection(url, username, password);

            // Create a table if it doesn't exist
            createTable(connection);

            // Save and retrieve records
            saveStudentRecord(connection, 1, "Elissa", 17, "RCA");
            saveStudentRecord(connection, 1, "Prince", 23, "University of Rwanda");
            retrieveStudentRecords(connection);

            // Close the connection
            connection.close();
        } catch (ClassNotFoundException | SQLException e) {
            e.printStackTrace();
            System.out.println("Failed to connect to the database😒");
        }
    }


    private static void retrieveStudentRecords(Connection connection) throws SQLException {
        String selectSQL = "SELECT * FROM students";
        try (PreparedStatement preparedStatement = connection.prepareStatement(selectSQL);
             ResultSet resultSet = preparedStatement.executeQuery()) {
            System.out.println("Student Records:");
            while (resultSet.next()) {
                int id = resultSet.getInt("id");
                String name = resultSet.getString("name");
                int age = resultSet.getInt("age");
                String school = resultSet.getString("school");
                System.out.println("ID: " + id + ", Name: " + name + ", Age: " + age + ", School: " + school);
            }
        }
    }
}
