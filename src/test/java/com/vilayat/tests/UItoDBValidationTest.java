package com.vilayat.tests;

import com.vilayat.base.BaseTest;
import com.vilayat.pages.GreenKartPage;
import com.vilayat.utils.ConfigReader;
import com.vilayat.utils.DBUtils;
import org.testng.Assert;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import java.sql.ResultSet;
import java.sql.SQLException;

public class UItoDBValidationTest extends BaseTest {
	
	 private GreenKartPage page;

    @BeforeClass
    public void setupDatabase() {
        System.out.println("--- Bootstrapping Cross-Layer DB Environment ---");
        DBUtils.connectToDatabase();
    }

    @Test
    public void verifyUIPriceMatchesDatabaseTruth() throws SQLException {
    	driver.get(ConfigReader.getBaseUrl());
        page = new GreenKartPage(driver, wait);
        page.waitForPageLoad();
        String targetProduct = "Brocolli";
        
        String query = "SELECT price FROM products WHERE product_name = ?;";
        ResultSet rs = DBUtils.executeParameterizedQuery(query, targetProduct);
        
        Assert.assertNotNull(rs, "Database query returned null.");
        Assert.assertTrue(rs.next(), "Product not found in database: " + targetProduct);
        
        double expectedDbPrice = rs.getDouble("price");
        System.out.println("Backend Truth: " + targetProduct + " price is " + expectedDbPrice);

        
        double actualUiPrice = page.getProductPrice(targetProduct); 
        System.out.println("Frontend Display: " + targetProduct + " price is " + actualUiPrice);

        Assert.assertEquals(actualUiPrice, expectedDbPrice, "Critical defect: UI price does not match Database price!");
        System.out.println("SUCCESS: Cross-Layer synergy achieved. Frontend accurately reflects Backend state.");
    }

    @AfterClass
    public void teardownDatabase() {
        System.out.println("--- Tearing Down Cross-Layer DB Environment ---");
        DBUtils.closeConnection();
    }
}