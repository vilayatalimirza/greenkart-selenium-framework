package com.vilayat.pages;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.PageFactory;

public class HomePage {

    private WebDriver driver;

    // Locators using @FindBy — PageFactory initializes these
    @FindBy(xpath = "//input[@type='search']")
    private WebElement searchBox;

    @FindBy(css = "img[alt='Cart']")
    private WebElement cartIcon;

    @FindBy(css = ".brand")
    private WebElement logo;

    private By topDealsLink = By.linkText("Top Deals");
    private By cartCount = By.cssSelector(".cart-info span");

    // Constructor — PageFactory.initElements is what makes @FindBy work
    public HomePage(WebDriver driver) {
        this.driver = driver;
        PageFactory.initElements(driver, this);
    }

    // Page Actions
    public void searchProduct(String productName) {
        searchBox.clear();
        searchBox.sendKeys(productName);
    }

    public void clickCart() {
        cartIcon.click();
    }

    public void clickTopDeals() {
        driver.findElement(topDealsLink).click();
    }

    public boolean isLogoDisplayed() {
        return logo.isDisplayed();
    }

    public String getLogoText() {
        return logo.getText();
    }

    public String getCartItemCount() {
        return driver.findElement(cartCount).getText();
    }

    public String getPageTitle() {
        return driver.getTitle();
    }

    public String getCurrentURL() {
        return driver.getCurrentUrl();
    }
}		