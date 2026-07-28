package com.vilayat.tests;

import java.time.Duration;
import java.util.Iterator;
import java.util.Set;

import org.openqa.selenium.support.ui.WebDriverWait;
import org.testng.Assert;
import org.testng.annotations.Test;

import com.vilayat.base.BaseTest;
import com.vilayat.pages.GreenKartPage;
import com.vilayat.pages.TopDealsPage;
import com.vilayat.utils.TestData;

public class TopDealsTest extends BaseTest {

    @Test
    public void verifyTopDealsSearch() {
        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(10));
        GreenKartPage homePage = new GreenKartPage(driver, wait);
        
        // 1. Navigate to the main app using TestData
        driver.get(TestData.APP_URL);
        
        // 2. Click Top Deals (This opens a NEW TAB)
        homePage.clickTopDeals();
        
        // 3. --- WINDOW SWITCHING LOGIC ---
        Set<String> windows = driver.getWindowHandles();
        Iterator<String> it = windows.iterator();
        String parentWindow = it.next();
        String childWindow = it.next();
        
        // Tell Selenium to switch focus to the new tab
        driver.switchTo().window(childWindow);
        
        // 4. Use TopDealsPage with TestData constants
        TopDealsPage topDealsPage = new TopDealsPage(driver, wait);
        
        topDealsPage.searchForDeal(TestData.SEARCH_VALID_TOMATO);
        String actualItem = topDealsPage.getSearchedItemName();
        
        // Assert using the same constant
        Assert.assertEquals(actualItem, TestData.SEARCH_VALID_TOMATO);
    }
}