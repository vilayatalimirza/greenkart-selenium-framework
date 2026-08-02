package com.vilayat.tests;

import java.util.Set;

import org.testng.Assert;
import org.testng.annotations.Test;

import com.vilayat.base.BaseTest;
import com.vilayat.pages.GreenKartPage;
import com.vilayat.pages.TopDealsPage;
import com.vilayat.utils.ConfigReader;
import com.vilayat.utils.TestData;

public class TopDealsTest extends BaseTest {

    @Test
    public void verifyTopDealsSearch() {
        GreenKartPage greenKartPage = new GreenKartPage(driver, wait);
        
        driver.get(ConfigReader.getBaseUrl());
        
        String parentWindow = driver.getWindowHandle(); // capture BEFORE clicking
        greenKartPage.clickTopDeals();
        Set<String> windows = driver.getWindowHandles();
        for (String handle : windows) {
            if (!handle.equals(parentWindow)) {
                driver.switchTo().window(handle);
                break;
            }
        }
 
        TopDealsPage topDealsPage = new TopDealsPage(driver, wait);
        
        topDealsPage.searchForDeal(TestData.SEARCH_VALID_TOMATO);
        String actualItem = topDealsPage.getSearchedItemName();
        
        // Assert using the same constant
        Assert.assertEquals(actualItem, TestData.SEARCH_VALID_TOMATO);
    }
}