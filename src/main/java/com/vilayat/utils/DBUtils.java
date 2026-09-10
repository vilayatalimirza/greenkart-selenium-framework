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

    public static ResultSet executeQuery(String query) {
        try {
            Statement statement = connection.createStatement();
            return statement.executeQuery(query);
        } catch (SQLException e) {
            System.err.println("ERROR: Query execution failed -> " + query);
            e.printStackTrace();
            return null;
        }
    }
    
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
    
    public static boolean upsertProduct(String productName, double price, String category) {
       
        String checkQuery = "SELECT product_id FROM products WHERE product_name = ?;";
        ResultSet rs = executeParameterizedQuery(checkQuery, productName);
        
        try {
            if (rs != null && rs.next()) {
                
                String updateQuery = "UPDATE products SET price = ? WHERE product_name = ?;";
                int rowsAffected = executeParameterizedUpdate(updateQuery, price, productName);
                System.out.println("TDM Sync [UPDATE]: Overwrote price for '" + productName + "' to " + price);
                return rowsAffected > 0;
            } else {
                
                String insertQuery = "INSERT INTO products (product_name, price, category) VALUES (?, ?, ?);";
                int rowsAffected = executeParameterizedUpdate(insertQuery, productName, price, category);
                System.out.println("TDM Sync [INSERT]: Created new product '" + productName + "' with price " + price);
                return rowsAffected > 0;
            }
        } catch (SQLException e) {
            System.err.println("FATAL: Upsert operation failed for product: " + productName);
            e.printStackTrace();
            return false;
        }
    }
    
    public static Object[][] getProductDataForTesting(String query) {
        System.out.println("Fetching test data from database for execution...");
        
        java.util.List<Object[]> dataList = new java.util.ArrayList<>();
        
        try {
            ResultSet rs = executeQuery(query);
            if (rs != null) {
                int columnCount = rs.getMetaData().getColumnCount();
                
                while (rs.next()) {
                    Object[] row = new Object[columnCount];
                    for (int i = 0; i < columnCount; i++) {
                        row[i] = rs.getObject(i + 1); 
                    }
                    dataList.add(row);
                }
            }
        } catch (SQLException e) {
            System.err.println("ERROR: Failed to fetch data for DataProvider.");
            e.printStackTrace();
        }
        
        return dataList.toArray(new Object[0][0]);
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