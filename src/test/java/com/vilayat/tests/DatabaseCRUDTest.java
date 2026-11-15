package com.vilayat.tests;

import com.vilayat.utils.DBUtils;
import org.testng.Assert;
import org.testng.annotations.Test;

import java.util.List;
import java.util.Map;
import java.util.UUID;

public class DatabaseCRUDTest {

    private final String testProductName = "Automation Apple " + UUID.randomUUID();

    @Test(priority = 1)
    public void createNewProductTest() {
        String insertQuery = "INSERT INTO products (product_name, price, category) VALUES (?, ?, ?)";
        int rowsAffected = DBUtils.executeParameterizedUpdate(
                insertQuery, testProductName, 150.00, "Fruit");
        Assert.assertEquals(rowsAffected, 1, "Product insertion failed.");
    }

    @Test(priority = 2, dependsOnMethods = "createNewProductTest")
    public void updateProductPriceTest() {
        String updateQuery = "UPDATE products SET price = ? WHERE product_name = ?";
        int rowsAffected = DBUtils.executeParameterizedUpdate(updateQuery, 175.50, testProductName);
        Assert.assertEquals(rowsAffected, 1, "Product update failed.");

        String selectQuery = "SELECT price FROM products WHERE product_name = ?";
        List<Map<String, Object>> rows =
                DBUtils.executeParameterizedQuery(selectQuery, testProductName);
        Assert.assertEquals(rows.size(), 1, "Updated product should be returned exactly once.");

        double actualPrice = ((Number) rows.get(0).get("price")).doubleValue();
        Assert.assertEquals(actualPrice, 175.50, 0.001, "Database price mismatch.");
    }

    @Test(priority = 3, dependsOnMethods = "updateProductPriceTest")
    public void deleteProductTest() {
        String deleteQuery = "DELETE FROM products WHERE product_name = ?";
        int rowsAffected = DBUtils.executeParameterizedUpdate(deleteQuery, testProductName);
        Assert.assertEquals(rowsAffected, 1, "Product deletion failed.");
    }
}
