package com.vilayat.tests;

import com.vilayat.base.BaseTest;
import com.vilayat.pages.GreenKartPage;
import com.vilayat.utils.ConfigReader;
import com.vilayat.utils.DBUtils;
import org.testng.Assert;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

public class DatabaseDrivenSearchTest extends BaseTest {

    @BeforeClass
    public void setupDatabase() {
        System.out.println("--- Bootstrapping Data-Driven Environment ---");
        DBUtils.connectToDatabase();
    }

    @DataProvider(name = "dbProductProvider")
    public Object[][] provideProductData() {
        String query = "SELECT product_name FROM products LIMIT 3;";
        return DBUtils.getProductDataForTesting(query);
    }

    @Test(dataProvider = "dbProductProvider")
    public void verifySearchFunctionalityUsingDBData(String dbProductName) {
        driver.get(ConfigReader.getBaseUrl());
        GreenKartPage page = new GreenKartPage(driver, wait);
        
        System.out.println("Executing UI Search Test for: " + dbProductName);

        page.searchProduct(dbProductName);
        java.util.List<String> searchResults = page.getVisibleProductNames();
        Assert.assertTrue(searchResults.contains(dbProductName), 
                "Search defect: Could not find " + dbProductName + " in UI results!");
        
        System.out.println("SUCCESS: Search verified for " + dbProductName);
    }

    @AfterClass
    public void teardownDatabase() {
        System.out.println("--- Tearing Down Data-Driven Environment ---");
        DBUtils.closeConnection();
    }
}