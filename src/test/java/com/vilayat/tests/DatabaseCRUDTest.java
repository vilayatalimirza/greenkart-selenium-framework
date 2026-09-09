package com.vilayat.tests;

import com.vilayat.utils.DBUtils;
import org.testng.Assert;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import java.sql.ResultSet;
import java.sql.SQLException;

public class DatabaseCRUDTest {

    private final String testProductName = "Automation Apple"; 

    @BeforeClass
    public void setup() {
        DBUtils.connectToDatabase();
    }

    @Test(priority = 1)
    public void createNewProductTest() {
        String insertQuery = "INSERT INTO products (product_name, price, category) VALUES (?, ?, ?);";
        
        int rowsAffected = DBUtils.executeParameterizedUpdate(insertQuery, testProductName, 150.00, "Fruit");
        Assert.assertEquals(rowsAffected, 1, "Product insertion failed.");
    }

    @Test(priority = 2, dependsOnMethods = "createNewProductTest")
    public void updateProductPriceTest() throws SQLException {
        String updateQuery = "UPDATE products SET price = ? WHERE product_name = ?;";
        int rowsAffected = DBUtils.executeParameterizedUpdate(updateQuery, 175.50, testProductName);
        Assert.assertEquals(rowsAffected, 1, "Product update failed.");

        String selectQuery = "SELECT price FROM products WHERE product_name = ?;";
        ResultSet rs = DBUtils.executeParameterizedQuery(selectQuery, testProductName);
        
        Assert.assertNotNull(rs);
        Assert.assertTrue(rs.next());
        Assert.assertEquals(rs.getDouble("price"), 175.50, "Database price mismatch.");
    }

    @Test(priority = 3, dependsOnMethods = "updateProductPriceTest")
    public void deleteProductTest() {
        String deleteQuery = "DELETE FROM products WHERE product_name = ?;";
        int rowsAffected = DBUtils.executeParameterizedUpdate(deleteQuery, testProductName);
        Assert.assertEquals(rowsAffected, 1, "Product deletion failed. Technical debt created.");
    }

    @AfterClass
    public void teardown() {
        DBUtils.closeConnection();
    }
}