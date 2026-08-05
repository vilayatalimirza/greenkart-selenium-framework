package com.vilayat.pages;

import java.util.ArrayList;
import java.util.List;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.WebDriverWait;
import com.vilayat.utils.WaitUtils;
import com.vilayat.exceptions.ProductNotFoundException;

public class CartPopupPage {
    private WebDriver driver;
    private WebDriverWait wait;

    // --- LOCATORS ---
    // We scope these specifically to the "active" popup container to prevent Selenium 
    // from accidentally interacting with hidden elements in the background.
    private By cartPreviewContainer = By.cssSelector(".cart-preview.active");
    private By cartItemRows = By.cssSelector(".cart-preview.active .cart-items li");
    private By proceedToCheckoutBtn = By.xpath("//div[contains(@class,'active')]//button[contains(text(),'PROCEED TO CHECKOUT')]");

    public CartPopupPage(WebDriver driver, WebDriverWait wait) {
        this.driver = driver;
        this.wait = wait;
    }

   
    public void waitForPopupToLoad() {
        WaitUtils.waitForVisible(wait, cartPreviewContainer);
    }

    
    private WebElement getPopupRowForProduct(String productName) {
        waitForPopupToLoad();
        List<WebElement> rows = driver.findElements(cartItemRows);
        
        for (WebElement row : rows) {
            String name = row.findElement(By.cssSelector(".product-name")).getText().split("-")[0].trim();
            if (name.equalsIgnoreCase(productName)) {
                return row;
            }
        }
        throw new ProductNotFoundException("Product not found in cart popup: " + productName);
    }

    public int getCartPopupQuantity(String productName) {
        WebElement row = getPopupRowForProduct(productName);
        String qtyText = row.findElement(By.cssSelector(".quantity")).getText();
        return Integer.parseInt(qtyText.replaceAll("[^0-9]", "")); 
    }

    public int getCartPopupUnitPrice(String productName) {
        WebElement row = getPopupRowForProduct(productName);
        String priceText = row.findElement(By.cssSelector(".product-price")).getText();
        return Integer.parseInt(priceText.replaceAll("[^0-9]", ""));
    }

    public int getCartPopupSubtotal(String productName) {
        WebElement row = getPopupRowForProduct(productName);
        String totalText = row.findElement(By.cssSelector(".amount")).getText();
        return Integer.parseInt(totalText.replaceAll("[^0-9]", ""));
    }

    public void removeCartPopupItem(String productName) {
        WebElement row = getPopupRowForProduct(productName);
        row.findElement(By.cssSelector(".product-remove")).click();
    }

    public CheckoutPage proceedToCheckout() {
    	waitForPopupToLoad();
    	WebElement btn = WaitUtils.waitForClickable(wait,proceedToCheckoutBtn);
    	((org.openqa.selenium.JavascriptExecutor) driver).executeScript("arguments[0].click();", btn);
    	WaitUtils.urlHasText(wait, "cart");
        return new CheckoutPage(driver, wait);
    }
}