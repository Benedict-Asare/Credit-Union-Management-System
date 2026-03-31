package com.creditunion;

import com.creditunion.config.DatabaseConfig;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;

public class TestDatabase {
    public static void main(String[] args) {
        System.out.println("=== Testing Database Connection ===\n");

        try (Connection conn = DatabaseConfig.getConnection()) {
            System.out.println("✓ Connected to SQL Server!");

            // Test query
            Statement stmt = conn.createStatement();
            ResultSet rs = stmt.executeQuery("SELECT @@VERSION as version");

            if (rs.next()) {
                System.out.println("✓ SQL Server Version: " + rs.getString("version").substring(0, 50) + "...");
            }
            rs.close();

            // Check if database exists
            rs = stmt.executeQuery("SELECT DB_NAME() as dbname");
            if (rs.next()) {
                System.out.println("✓ Connected to database: " + rs.getString("dbname"));
            }
            rs.close();
            stmt.close();

            System.out.println("\n✓ Database is ready to use!");

        } catch (Exception e) {
            System.err.println("✗ Connection failed!");
            System.err.println("Error: " + e.getMessage());
            System.err.println("\nTroubleshooting:");
            System.err.println("1. Make sure SA password is correct");
            System.err.println("2. Run in SSMS: ALTER LOGIN sa WITH PASSWORD = 'Admin123!';");
            System.err.println("3. Enable Mixed Mode Authentication in SQL Server Properties");
            System.err.println("4. Restart SQL Server service");
        }
    }
}