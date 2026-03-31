package com.creditunion.config;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DatabaseConfig {
    // Use SQL Authentication with SA account (NOT Windows Authentication)
    private static final String URL = "jdbc:sqlserver://localhost:1433;databaseName=CreditUnionSystemDB;encrypt=false;trustServerCertificate=true";
    private static final String USERNAME = "sa";
    private static final String PASSWORD = "Admin123!"; // Change this to your actual SA password

    private static Connection connection = null;

    static {
        try {
            Class.forName("com.microsoft.sqlserver.jdbc.SQLServerDriver");
            System.out.println("SQL Server JDBC Driver loaded successfully");
        } catch (ClassNotFoundException e) {
            System.err.println("SQL Server JDBC Driver not found!");
            e.printStackTrace();
        }
    }

    public static Connection getConnection() throws SQLException {
        try {
            if (connection == null || connection.isClosed()) {
                // Use SQL Authentication with username and password
                connection = DriverManager.getConnection(URL, USERNAME, PASSWORD);
                System.out.println("✓ Database connection established successfully to CreditUnionSystemDB");
            }
            return connection;
        } catch (SQLException e) {
            System.err.println("✗ Failed to connect to database");
            System.err.println("URL: " + URL);
            System.err.println("Username: " + USERNAME);
            System.err.println("Error: " + e.getMessage());
            throw e;
        }
    }

    public static void testConnection() {
        try (Connection conn = getConnection()) {
            System.out.println("✓ Database connection test: SUCCESS");
            System.out.println("  Database: CreditUnionSystemDB");
        } catch (SQLException e) {
            System.err.println("✗ Database connection test: FAILED");
            System.err.println("  Error: " + e.getMessage());
        }
    }

    public static void closeConnection() {
        if (connection != null) {
            try {
                connection.close();
                System.out.println("Database connection closed");
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }
}