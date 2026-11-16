package com.vilayat.tests;

import com.vilayat.base.BaseTest;
import com.vilayat.pages.GreenKartPage;
import com.vilayat.utils.ConfigReader;
import com.vilayat.utils.DBUtils;
import org.testng.Assert;
import org.testng.annotations.Test;

import java.util.List;

public class DatabaseSyncTest extends BaseTest {

    @Test
    public void synchronizeAllProductsFromUI() {
        driver.get(ConfigReader.getBaseUrl());
        GreenKartPage page = new GreenKartPage(driver, wait);
        page.waitForPageLoad();

        System.out.println("Scanning GreenKart UI for live product catalog...");

        List<String> liveProducts = page.getVisibleProductNames();
        System.out.println("Found " + liveProducts.size() + " products on the UI. Initiating Upsert...");

        int successCount = 0;
        int failureCount = 0;

        for (String productName : liveProducts) {
            double livePrice = page.getProductPrice(productName);
            
            boolean isSuccess = DBUtils.upsertProduct(productName, livePrice, null);
            
            if (isSuccess) {
                successCount++;
            } else {
                failureCount++;
            }
        }

        System.out.println("==================================================");
        System.out.println("TDM Sync Complete!");
        System.out.println("Successfully synchronized: " + successCount);
        System.out.println("Failed to synchronize: " + failureCount);
        System.out.println("Target: configured local test database (not the GreenKart application's backend).");
        System.out.println("==================================================");

        Assert.assertEquals(successCount, liveProducts.size(),
                "One or more UI products could not be synchronized to the local test database.");
    }

}