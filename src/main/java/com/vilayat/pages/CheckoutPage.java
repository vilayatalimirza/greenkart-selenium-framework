package com.vilayat.pages;

import java.util.ArrayList;
import java.util.List;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.WebDriverWait;
import com.vilayat.utils.WaitUtils;
import com.vilayat.exceptions.ProductNotFoundException;

public class CheckoutPage {
    private WebDriver driver;
    private WebDriverWait wait;

    private By tableRows = By.cssSelector("table#productTable tbody tr");
    private By promoCodeInput = By.cssSelector("input.promoCode");
    private By applyPromoBtn = By.cssSelector("button.promoBtn");
    private By promoInfoMsg = By.cssSelector("span.promoInfo");
    private By totalAmount = By.cssSelector("span.totAmt");
    private By discountPercentage = By.cssSelector("span.discountPerc");
    private By placeOrderBtn = By.xpath("//button[contains(text(),'Place Order')]");

    public CheckoutPage(WebDriver driver, WebDriverWait wait) {
        this.driver = driver;
        this.wait = wait;
    }

    private WebElement getCheckoutRowForProduct(String productName) {
        WaitUtils.waitForVisible(wait, tableRows);
        List<WebElement> rows = driver.findElements(tableRows);
        
        for (WebElement row : rows) {
            String name = row.findElement(By.cssSelector("td:nth-child(2) p.product-name")).getText().split("-")[0].trim();
            if (name.equalsIgnoreCase(productName)) {
                return row;
            }
        }
        throw new ProductNotFoundException("Product not found in Checkout Table: " + productName);
    }
    
    public List<String> getCheckoutProductNames() {
        WaitUtils.waitForVisible(wait, tableRows);
        List<WebElement> rows = driver.findElements(tableRows);
        List<String> names = new ArrayList<>();
        
        for (WebElement row : rows) {
            names.add(row.findElement(By.cssSelector("td:nth-child(2) p.product-name")).getText().split("-")[0].trim());
        }
        return names;
    }

    public int getCheckoutAmountForProduct(String productName) {
        WebElement row = getCheckoutRowForProduct(productName);
        String amountText = row.findElement(By.cssSelector("td:nth-child(4) p.amount")).getText();
        return Integer.parseInt(amountText.replaceAll("[^0-9]", ""));
    }

    public int getTotalAmount() {
        String total = WaitUtils.waitForVisible(wait, totalAmount).getText();
        return Integer.parseInt(total.replaceAll("[^0-9]", ""));
    }

    public void applyPromoCode(String code) {
        WaitUtils.waitForVisible(wait, promoCodeInput).sendKeys(code);
        driver.findElement(applyPromoBtn).click();
    }

    public String getPromoMessage() {
        return WaitUtils.waitForVisible(wait, promoInfoMsg).getText();
    }

    public double getDiscountPercentage() {
        String discountText = WaitUtils.waitForVisible(wait, discountPercentage).getText();
        return Double.parseDouble(discountText.replace("%", "").trim());
    }

    public PlaceOrderPage clickPlaceOrder() {
        WaitUtils.waitForVisible(wait, placeOrderBtn).click();
        return new PlaceOrderPage(driver, wait);
    }
}