package com.creditunion.util;

import java.sql.Connection;
import java.sql.DriverManager;

public class DatabaseConnection {

    public static Connection getConnection() {

        Connection connection = null;

        try {

            String url = "jdbc:sqlserver://localhost:1433;"
                    + "databaseName=CreditUnionDB;"
                    + "encrypt=false;"
                    + "integratedSecurity=true";

            connection = DriverManager.getConnection(url);

            System.out.println("Connected to SQL Server successfully!");

        } catch (Exception e) {
            e.printStackTrace();
        }

        return connection;
    }
}
