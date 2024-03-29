import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Scanner;

public class JdbcStudents {

    public static void main(String[] args) {
        try {
            // Load the JDBC driver
            Class.forName("com.mysql.cj.jdbc.Driver");

            // Replace these placeholders with your MySQL database connection details
            String url = "jdbc:mysql://localhost:3306/jdbc_students_2";
            String username = "root";
            String password = "";

            // Establish the connection
            Connection connection = DriverManager.getConnection(url, username, password);

            // Create a table if it doesn't exist
            createTable(connection);

            // Save and retrieve records
            saveStudentRecord(connection, 1, "Elissa", 17, "RCA");
            saveStudentRecord(connection, 2, "Prince", 23, "University of Rwanda");
            retrieveStudentRecords(connection);

            Scanner scanner = new Scanner(System.in);
            System.out.println("Do you want to delete a student at a certain ID? (y/n): ");
            String choice = scanner.nextLine().trim().toLowerCase();

            //Check if yes to call delete method
            if(choice.equals("y")) {
                deleteStudentRecord(connection);
            }

            // Close the connection
            connection.close();
        } catch (ClassNotFoundException | SQLException e) {
            e.printStackTrace();
            System.out.println("Failed to connect to the database😒");
        }
    }

    private static void createTable(Connection connection) throws SQLException {
        String createTableSQL = "CREATE TABLE IF NOT EXISTS students (id INT PRIMARY KEY, name VARCHAR(255), age INT, school VARCHAR(255))";
        try (PreparedStatement preparedStatement = connection.prepareStatement(createTableSQL)) {
            preparedStatement.execute();
        }
    }

    private static void saveStudentRecord(Connection connection, int id, String name, int age, String school) throws SQLException {
        // Check if a student with the same ID already exists
        if (studentExists(connection, id)) {
            System.out.println("Student with ID " + id + " already exists. Skipping insertion.");
            return;
        }

        // If not, insert the new record
        String insertSQL = "INSERT INTO students (id, name, age, school) VALUES (?, ?, ?, ?)";
        try (PreparedStatement preparedStatement = connection.prepareStatement(insertSQL)) {
            preparedStatement.setInt(1, id);
            preparedStatement.setString(2, name);
            preparedStatement.setInt(3, age);
            preparedStatement.setString(4, school);
            preparedStatement.execute();
            System.out.println("Student with ID " + id + " inserted successfully.");
        }
    }

    private static boolean studentExists(Connection connection, int id) throws SQLException {
        String selectSQL = "SELECT id FROM students WHERE id = ?";
        try (PreparedStatement preparedStatement = connection.prepareStatement(selectSQL)) {
            preparedStatement.setInt(1, id);
            try (ResultSet resultSet = preparedStatement.executeQuery()) {
                return resultSet.next(); // If resultSet.next() is true, a student with the given ID exists
            }
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
    private static void deleteStudentRecord(Connection connection) throws SQLException {
        Scanner scanner = new Scanner(System.in);
        System.out.println("Enter the ID of the student to delete: ");
        int idToDelete = scanner.nextInt();
        //Check if the student with the given ID exists before attempting deletion
        if(!studentExists(connection, idToDelete)) {
            System.out.println("Student with ID " + idToDelete + " does not exist, ...Skipping deletion😒");
            return;
        }
        //Then, if the student exists, delete the record
        String deleteSQL = "DELETE FROM students WHERE id=?";
        try (PreparedStatement preparedStatement = connection.prepareStatement(deleteSQL)) {
            preparedStatement.setInt(1, idToDelete);
            int rowsAffected = preparedStatement.executeUpdate();

            if(rowsAffected > 0){
                System.out.println("Student with ID " + idToDelete + " deleted successfully.");
            } else {
                System.out.println("Failed to delete student with ID " + idToDelete + ". No matching record found.");
            }
        }
    }
}

