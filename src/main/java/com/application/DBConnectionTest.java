package com.application;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DBConnectionTest {
    private static final String URL = "jdbc:mysql://localhost:3306/db_jobportal";
    private static final String USERNAME = "root";  // Replace with your database username
    private static final String PASSWORD = "root";  // Replace with your database password

    // This method will return a connection to the database
    public static Connection getConnection() {
        try {
            return DriverManager.getConnection(URL, USERNAME, PASSWORD);
        } catch (SQLException e) {
            System.out.println("Database connection failed!");
            e.printStackTrace();
            return null;
        }
    }

    public static void main(String[] args) {
        // You can keep the main method to test the connection independently if needed
        Connection connection = getConnection();
        if (connection != null) {
            System.out.println("Database connection successful!");
        }
    }
}
