package com.vilayat.utils;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * JDBC helper for the GreenKart test database.
 * Each operation owns its connection and closes all JDBC resources before returning.
 */
public final class DBUtils {

    private DBUtils() {
        // Utility class
    }

    private static Connection openConnection() throws SQLException {
        String url = ConfigReader.getProperty("db.url");
        String username = ConfigReader.getProperty("db.username");
        String password = ConfigReader.getProperty("db.password");

        if (url == null || url.isBlank()) {
            throw new IllegalStateException("Database URL is missing. Set DB_URL or db.url.");
        }
        if (username == null || username.isBlank()) {
            throw new IllegalStateException("Database username is missing. Set DB_USERNAME or db.username.");
        }
        if (password == null) {
            throw new IllegalStateException("Database password is missing. Set DB_PASSWORD or db.password.");
        }

        return DriverManager.getConnection(url, username, password);
    }

    private static void bindParameters(PreparedStatement statement, Object... params)
            throws SQLException {
        for (int i = 0; i < params.length; i++) {
            statement.setObject(i + 1, params[i]);
        }
    }

    /**
     * Executes a parameterized SELECT and returns detached row data.
     * JDBC resources are closed before the caller receives the result.
     */
    public static List<Map<String, Object>> executeParameterizedQuery(
            String query, Object... params) {
        try (Connection connection = openConnection();
             PreparedStatement statement = connection.prepareStatement(query)) {

            bindParameters(statement, params);

            try (ResultSet resultSet = statement.executeQuery()) {
                return readRows(resultSet);
            }
        } catch (SQLException e) {
            throw new IllegalStateException(
                    "Database query failed. Check the SQL and database configuration.", e);
        }
    }

    public static List<Map<String, Object>> executeQuery(String query) {
        return executeParameterizedQuery(query);
    }

    private static List<Map<String, Object>> readRows(ResultSet resultSet) throws SQLException {
        List<Map<String, Object>> rows = new ArrayList<>();
        ResultSetMetaData metadata = resultSet.getMetaData();
        int columnCount = metadata.getColumnCount();

        while (resultSet.next()) {
            Map<String, Object> row = new LinkedHashMap<>();
            for (int column = 1; column <= columnCount; column++) {
                row.put(metadata.getColumnLabel(column), resultSet.getObject(column));
            }
            rows.add(row);
        }

        return rows;
    }

    public static int executeParameterizedUpdate(String query, Object... params) {
        try (Connection connection = openConnection();
             PreparedStatement statement = connection.prepareStatement(query)) {

            bindParameters(statement, params);
            return statement.executeUpdate();
        } catch (SQLException e) {
            throw new IllegalStateException(
                    "Database update failed. Check the SQL and database configuration.", e);
        }
    }

    /**
     * Performs the catalog upsert using one short-lived connection.
     * Category may be null when the source UI does not expose category data.
     */
    public static boolean upsertProduct(String productName, double price, String category) {
        String selectSql = "SELECT product_id FROM products WHERE product_name = ? ORDER BY product_id LIMIT 1";
        String updateSql = "UPDATE products SET price = ?, category = COALESCE(?, category) WHERE product_id = ?";
        String insertSql = "INSERT INTO products (product_name, price, category) VALUES (?, ?, ?)";

        try (Connection connection = openConnection();
             PreparedStatement select = connection.prepareStatement(selectSql)) {

            select.setString(1, productName);
            Integer productId = null;

            try (ResultSet resultSet = select.executeQuery()) {
                if (resultSet.next()) {
                    productId = resultSet.getInt("product_id");
                }
            }

            if (productId != null) {
                try (PreparedStatement update = connection.prepareStatement(updateSql)) {
                    update.setDouble(1, price);
                    update.setString(2, category);
                    update.setInt(3, productId);
                    update.executeUpdate();
                    return true;
                }
            }

            try (PreparedStatement insert = connection.prepareStatement(insertSql)) {
                insert.setString(1, productName);
                insert.setDouble(2, price);
                insert.setString(3, category);
                return insert.executeUpdate() == 1;
            }
        } catch (SQLException e) {
            throw new IllegalStateException("Product upsert failed for '" + productName + "'.", e);
        }
    }

    public static Object[][] getProductDataForTesting(String query) {
        List<Map<String, Object>> rows = executeQuery(query);
        if (rows.isEmpty()) {
            return new Object[0][0];
        }

        List<String> columns = new ArrayList<>(rows.get(0).keySet());
        Object[][] data = new Object[rows.size()][columns.size()];

        for (int rowIndex = 0; rowIndex < rows.size(); rowIndex++) {
            for (int columnIndex = 0; columnIndex < columns.size(); columnIndex++) {
                data[rowIndex][columnIndex] = rows.get(rowIndex).get(columns.get(columnIndex));
            }
        }

        return data;
    }
}
