package com.vilayat.utils;

import java.sql.*;

public class DBUtils {

    private static Connection connection;

    public static void connectToDatabase() {
        try {
            if (connection == null || connection.isClosed()) {
                String dbUrl = ConfigReader.getProperty("db.url");
                String dbUser = ConfigReader.getProperty("db.username");
                String dbPass = ConfigReader.getProperty("db.password");
                
                connection = DriverManager.getConnection(dbUrl, dbUser, dbPass);
                System.out.println("SUCCESS: JDBC Connection established to " + dbUrl);
            }
        } catch (SQLException e) {
            System.err.println("FATAL: Database connection failed!");
            e.printStackTrace();
            throw new RuntimeException("Database connection failed.");
        }
    }

    // Refactored: Enterprise SELECT with dynamic parameters
    public static ResultSet executeParameterizedQuery(String query, Object... params) {
        try {
            PreparedStatement pstmt = connection.prepareStatement(query);
            for (int i = 0; i < params.length; i++) {
                pstmt.setObject(i + 1, params[i]);
            }
            System.out.println("Executing SELECT Query: " + pstmt.toString());
            return pstmt.executeQuery();
        } catch (SQLException e) {
            System.err.println("ERROR: Query execution failed!");
            e.printStackTrace();
            return null;
        }
    }

    // Refactored: Enterprise DML (INSERT, UPDATE, DELETE) with dynamic parameters
    public static int executeParameterizedUpdate(String query, Object... params) {
        try {
            PreparedStatement pstmt = connection.prepareStatement(query);
            for (int i = 0; i < params.length; i++) {
                pstmt.setObject(i + 1, params[i]);
            }
            System.out.println("Executing DML Query: " + pstmt.toString());
            int rowsAffected = pstmt.executeUpdate();
            System.out.println("Rows affected: " + rowsAffected);
            return rowsAffected;
        } catch (SQLException e) {
            System.err.println("ERROR: DML execution failed!");
            e.printStackTrace();
            return -1;
        }
    }

    public static void closeConnection() {
        try {
            if (connection != null && !connection.isClosed()) {
                connection.close();
                System.out.println("SUCCESS: JDBC Connection closed gracefully.");
            }
        } catch (SQLException e) {
            System.err.println("ERROR: Failed to close database connection.");
            e.printStackTrace();
        }
    }
}