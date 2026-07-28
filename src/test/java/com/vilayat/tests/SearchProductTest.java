package com.vilayat.tests;

import com.vilayat.base.BaseTest;
import com.vilayat.pages.GreenKartPage;
import com.vilayat.utils.ConfigReader;
import com.vilayat.utils.TestData;
import org.testng.Assert;
import org.testng.annotations.Test;

import java.util.List;

public class SearchProductTest extends BaseTest {

    @Test
    public void verifySearchFiltersProductList() {
        driver.get(ConfigReader.getBaseUrl());

        GreenKartPage greenKartPage = new GreenKartPage(driver, wait);
        Assert.assertTrue(greenKartPage.isLogoDisplayed(), "Logo should be visible on page load");

        greenKartPage.searchProduct(TestData.SEARCH_VALID_FULL);
        List<String> visibleProducts = greenKartPage.getVisibleProductNames();

        for (String productName : visibleProducts) {
            Assert.assertTrue(
                productName.toLowerCase().contains(TestData.SEARCH_VALID_FULL.toLowerCase()),
                "Expected only products containing '" + TestData.SEARCH_VALID_FULL + "' but found: " + productName);
        }
    }
}