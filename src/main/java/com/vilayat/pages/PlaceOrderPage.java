package com.vilayat.pages;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.support.ui.Select;
import org.openqa.selenium.support.ui.WebDriverWait;
import com.vilayat.utils.WaitUtils;

public class PlaceOrderPage {
    private WebDriver driver;
    private WebDriverWait wait;

    private By countryDropdown = By.tagName("select");
    private By termsCheckbox = By.cssSelector("input.chkAgree");
    private By proceedBtn = By.xpath("//button[contains(text(),'Proceed')]");
    
    private By successMessage = By.xpath("//*[contains(text(), 'Thank you, your order has been placed successfully')]");

    public PlaceOrderPage(WebDriver driver, WebDriverWait wait) {
        this.driver = driver;
        this.wait = wait;
    }

    
    public void selectCountry(String countryName) {
        Select dropdown = new Select(WaitUtils.waitForVisible(wait, countryDropdown));
        dropdown.selectByVisibleText(countryName);
    }

    public void agreeToTerms() {
        WaitUtils.waitForVisible(wait, termsCheckbox).click();
    }

    public void clickProceed() {
        driver.findElement(proceedBtn).click();
    }

    public boolean isOrderSuccessful() {
        return WaitUtils.waitForVisible(wait, successMessage).isDisplayed();
    }
}