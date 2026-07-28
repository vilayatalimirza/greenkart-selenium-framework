package com.vilayat.pages;

import java.util.Arrays;
import java.util.List;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import com.vilayat.utils.WaitUtils;
import com.vilayat.exceptions.ProductNotFoundException;
import org.openqa.selenium.support.ui.Select;
import org.openqa.selenium.support.ui.WebDriverWait;

public class GreenKartPage {
	private WebDriver driver;
    private WebDriverWait wait;
    
    private By productNames = By.cssSelector("h4.product-name");
    private By addToCartButtons = By.xpath("//div[@class='product-action']/button");
    private By cartIcon = By.cssSelector("img[alt='Cart']");
    private By proceedToCheckoutBtn = By.xpath("//button[contains(text(),'PROCEED TO CHECKOUT')]");
    private By promoCodeInput = By.cssSelector("input.promoCode");
    private By promoApplyBtn = By.cssSelector("button.promoBtn");
    private By promoInfoText = By.cssSelector("span.promoInfo");
    private By placeOrderBtn = By.xpath("//button[text()='Place Order']");
    private By countryDropdown = By.xpath("//select[@style='width: 200px;']");
    private By termsCheckbox = By.cssSelector("input.chkAgree");
    private By proceedFinalBtn = By.cssSelector("button");
    private By searchBox = By.xpath("//input[@type='search']");
    private By logo = By.cssSelector(".brand");
    private By topDealsLink = By.linkText("Top Deals");
    private By cartCount = By.cssSelector(".cart-info span");
    
    public GreenKartPage(WebDriver driver, WebDriverWait wait) {
        this.driver = driver;
        this.wait = wait;
    }
    
    public void addItemsToCart(String[] items_needed) {
        WaitUtils.waitForVisible(wait, productNames);

        int itemfoundcount = 0;
        List<WebElement> products = driver.findElements(productNames);
        List<String> itemsNeededList = Arrays.asList(items_needed);

        for (int i = 0; i < products.size(); i++) {
            String fn = products.get(i).getText().split("-")[0].trim();

            if (itemsNeededList.contains(fn)) {
                itemfoundcount++;
                driver.findElements(addToCartButtons).get(i).click();

                if (itemfoundcount == items_needed.length)
                    break;
            }
        }

        if (itemfoundcount < items_needed.length) {
            throw new ProductNotFoundException(
                "Expected to add " + items_needed.length + " item(s) to cart, but only found "
                + itemfoundcount + ". Requested items: " + itemsNeededList
            );
        }
    }
    
    public void proceedToCheckout() {
        WaitUtils.waitForClickable(wait, cartIcon);
        driver.findElement(cartIcon).click();
        WaitUtils.waitForClickable(wait, proceedToCheckoutBtn);
        driver.findElement(proceedToCheckoutBtn).click();
    }

    public void applyPromoCode(String promoCode) {
        WaitUtils.waitForVisible(wait, promoCodeInput);
        driver.findElement(promoCodeInput).sendKeys(promoCode);
        WaitUtils.waitForClickable(wait, promoApplyBtn);
        driver.findElement(promoApplyBtn).click();
    }

    public String getPromoInfoText() {
        WaitUtils.waitForVisible(wait, promoInfoText);
        return driver.findElement(promoInfoText).getText();
    }

    public void placeOrder(String country) {
        WaitUtils.waitForClickable(wait, placeOrderBtn);
        driver.findElement(placeOrderBtn).click();

        WaitUtils.waitForVisible(wait, countryDropdown);
        Select select = new Select(driver.findElement(countryDropdown));
        select.selectByVisibleText(country);

        WaitUtils.waitForClickable(wait, termsCheckbox);
        driver.findElement(termsCheckbox).click();

        WaitUtils.waitForClickable(wait, proceedFinalBtn);
        driver.findElement(proceedFinalBtn).click();
    }
    public void searchProduct(String productName) {
        driver.findElement(searchBox).clear();
        driver.findElement(searchBox).sendKeys(productName);
    }

    public boolean isLogoDisplayed() {
        return driver.findElement(logo).isDisplayed();
    }

    public void clickTopDeals() {
        driver.findElement(topDealsLink).click();
    }

    public String getCartItemCount() {
        return driver.findElement(cartCount).getText();
    }
    public java.util.List<String> getVisibleProductNames() {
        com.vilayat.utils.WaitUtils.waitForVisible(wait, productNames);
        java.util.List<WebElement> products = driver.findElements(productNames);
        java.util.List<String> names = new java.util.ArrayList<>();
        for (WebElement p : products) {
            names.add(p.getText().split("-")[0].trim());
        }
        return names;
    }
}
