package com.vilayat.tests;

import com.vilayat.utils.DBUtils;
import org.testng.Assert;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import java.sql.ResultSet;
import java.sql.SQLException;

public class DatabaseSmokeTest {

    @BeforeClass
    public void setupDatabaseConnection() {
        DBUtils.connectToDatabase();
    }

    @Test
    public void verifyProductRetrievalFromDatabase() throws SQLException {
        // Arrange: Parameterized query for enterprise security
        String query = "SELECT price, category FROM products WHERE product_name = ?;";
        String targetProduct = "Brocolli"; 
        
        // Act: Execute query using Database Utility
        ResultSet resultSet = DBUtils.executeParameterizedQuery(query, targetProduct);
        
        // Assert: Validate the data integrity
        Assert.assertNotNull(resultSet, "ResultSet returned null.");
        Assert.assertTrue(resultSet.next(), "No data found for product: " + targetProduct);

        // Extract data and assert against definitive baseline
        double actualPrice = resultSet.getDouble("price");
        String actualCategory = resultSet.getString("category");

        Assert.assertEquals(actualPrice, 60.00, "Product price mismatch in DB!"); // Aligned to 60.00
        Assert.assertEquals(actualCategory, "Vegetable", "Product category mismatch in DB!"); // Aligned to singular 'Vegetable'
    }

    @AfterClass
    public void teardownDatabaseConnection() {
        DBUtils.closeConnection();
    }
}