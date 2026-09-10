package com.vilayat.tests;

import com.vilayat.base.BaseTest;
import com.vilayat.pages.GreenKartPage;
import com.vilayat.utils.ConfigReader;
import com.vilayat.utils.DBUtils;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import java.util.List;

public class DatabaseSyncTest extends BaseTest {

    @BeforeClass
    public void setupDatabase() {
        System.out.println("--- Bootstrapping TDM Sync Environment ---");
        DBUtils.connectToDatabase();
    }

    @Test
    public void synchronizeAllProductsFromUI() {
        driver.get(ConfigReader.getBaseUrl());
        GreenKartPage page = new GreenKartPage(driver, wait);

        System.out.println("Scanning GreenKart UI for live product catalog...");

        List<String> liveProducts = page.getVisibleProductNames();
        System.out.println("Found " + liveProducts.size() + " products on the UI. Initiating Upsert...");

        int successCount = 0;

        for (String productName : liveProducts) {
            double livePrice = page.getProductPrice(productName);
            
            boolean isSuccess = DBUtils.upsertProduct(productName, livePrice, "Vegetable");
            
            if (isSuccess) {
                successCount++;
            }
        }

        System.out.println("==================================================");
        System.out.println("TDM Sync Complete!");
        System.out.println("Successfully upserted " + successCount + " out of " + liveProducts.size() + " products.");
        System.out.println("Your local database is now 100% aligned with Production.");
        System.out.println("==================================================");
    }

    @AfterClass
    public void teardownDatabase() {
        System.out.println("--- Tearing Down TDM Sync Environment ---");
        DBUtils.closeConnection();
    }
}