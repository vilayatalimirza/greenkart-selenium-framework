package com.vilayat.pages;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.support.ui.WebDriverWait;
import com.vilayat.utils.WaitUtils;

public class TopDealsPage {
    
    private WebDriver driver;
    private WebDriverWait wait;
    
    // --- Locators ---
    private By searchBox = By.id("search-field");
    private By itemNameColumn = By.cssSelector("table.table-bordered tbody tr td:nth-child(1)");
    private By discountPriceColumn = By.cssSelector("table.table-bordered tbody tr td:nth-child(3)");
    private By dateSelector = By.cssSelector(".react-date-picker__inputGroup");

    // --- Constructor ---
    public TopDealsPage(WebDriver driver, WebDriverWait wait) {
        this.driver = driver;
        this.wait = wait;
    }
    
    // --- Page Actions ---
    
    public void searchForDeal(String productName) {
        WaitUtils.waitForVisible(wait, searchBox);
        driver.findElement(searchBox).clear();
        driver.findElement(searchBox).sendKeys(productName);
    }
    
    public String getSearchedItemName() {
        WaitUtils.waitForVisible(wait, itemNameColumn);
        return driver.findElement(itemNameColumn).getText();
    }
    
    public String getDiscountedPrice() {
        WaitUtils.waitForVisible(wait, discountPriceColumn);
        return driver.findElement(discountPriceColumn).getText();
    }
}