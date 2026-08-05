package com.vilayat.pages;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.Keys;
import org.openqa.selenium.TimeoutException;

import com.vilayat.utils.WaitUtils;
import com.vilayat.exceptions.ProductNotFoundException;

public class HomePage {
    private WebDriver driver;
    private WebDriverWait wait;
    
    private By searchBox = By.xpath("//input[@type='search']");
    private By logo = By.cssSelector(".brand");
    private By topDealsLink = By.linkText("Top Deals");
    private By flightBookingLink = By.linkText("Flight Booking");
    private By cartCount = By.cssSelector(".cart-info tr:nth-child(1) strong");
    private By cartTotal = By.cssSelector(".cart-info tr:nth-child(2) strong");
    private By productNames = By.cssSelector("h4.product-name");
    private By addToCartButtons = By.xpath("//div[@class='product-action']/button");
    private By cartIcon = By.cssSelector("img[alt='Cart']");
    private By incrementButtons = By.cssSelector("a.increment");
    private By decrementButtons = By.cssSelector("a.decrement");
    private By quantityInputs = By.cssSelector("input.quantity");
    
    public HomePage(WebDriver driver, WebDriverWait wait) {
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
    
    public void searchProduct(String productName) {
    	WaitUtils.waitForVisible(wait, productNames);
        WebElement firstProductBeforeSearch = driver.findElements(this.productNames).get(0);
        driver.findElement(searchBox).clear();
        driver.findElement(searchBox).sendKeys(productName);
        try {
            wait.until(ExpectedConditions.stalenessOf(firstProductBeforeSearch));
        } catch (Exception e) {}
    }
    public void clearSearchBox() {
        WebElement box = driver.findElement(searchBox);
        String currentValue = box.getDomProperty("value");
        for (int i = 0; i < currentValue.length(); i++) {
            box.sendKeys(Keys.BACK_SPACE);
        }
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
    
    public boolean isProductListEmpty() {
        return getVisibleProductNames().isEmpty();
    }
    
    public List<String> getVisibleProductNames() {
    	
    	List<String> visibleNames = new ArrayList<>();
    	try {
    	
	    	WaitUtils.waitForVisible(wait, productNames);
	        List<WebElement> products = driver.findElements(this.productNames);
	        
	        for (int i = 0; i < products.size(); i++) {
	            visibleNames.add(products.get(i).getText().split("-")[0].trim());
        	}
        }catch(TimeoutException e){
    		return visibleNames;
    	}
        return visibleNames;
    }
    
    public String getPageTitle() {
        return driver.getTitle();
    }

    public boolean isCartIconDisplayed() {
        return WaitUtils.waitForVisible(wait,cartIcon).isDisplayed();
    }
    
    public String getHomepageCartTotal() {
        return driver.findElement(cartTotal).getText();
    }
    public int getProductCount() {
    	WaitUtils.waitForVisible(wait, productNames);
        return driver.findElements(productNames).size();
    }

    public int getAddToCartButtonCount() {
    	WaitUtils.waitForVisible(wait, productNames);
        return driver.findElements(addToCartButtons).size();
    }

    public boolean isSearchBoxEnabled() {
        return driver.findElement(searchBox).isEnabled();
    }

    public boolean isTopDealsLinkDisplayed() {
        return driver.findElement(topDealsLink).isDisplayed();
    }

    public boolean isFlightBookingLinkDisplayed() {
        return driver.findElement(flightBookingLink).isDisplayed();
    }
    
    public int findProductIndex(String productName) {
    	WaitUtils.waitForVisible(wait, productNames);
        List<String> names = getVisibleProductNames();
        for (int i = 0; i < names.size(); i++) {
            if (names.get(i).equalsIgnoreCase(productName)) {
                return i;
            }
        }
        throw new ProductNotFoundException("Could not find product for quantity operation: " + productName);
    }
    
    public void increaseQuantity(String productName, int times) {
        int index = findProductIndex(productName);
        WebElement btn = driver.findElements(incrementButtons).get(index);
        for (int i = 0; i < times; i++) {
            btn.click();
        }
    }

    public void decreaseQuantity(String productName, int times) {
        int index = findProductIndex(productName);
        WebElement btn = driver.findElements(decrementButtons).get(index);
        for (int i = 0; i < times; i++) {
            btn.click();
        }
    }

    public String getQuantityValue(String productName) {
        int index = findProductIndex(productName);
        return driver.findElements(quantityInputs).get(index).getDomProperty("value");
    }

    public void addProductToCartByName(String productName) {
    	By dynamicButton = By.xpath("//h4[contains(text(), '" + productName + "')]/parent::div//button");
        WebElement element = driver.findElement(dynamicButton);
        ((JavascriptExecutor) driver).executeScript("arguments[0].scrollIntoView({block: 'center'});", element);
        element.click();
        WaitUtils.waitForText(wait, element, "ADDED"); 
    }
    public String getAddToCartButtonText(String productName) {
    	By dynamicButton = By.xpath("//h4[contains(text(), '" + productName + "')]/parent::div//button");
    	return driver.findElement(dynamicButton).getText();
    }
    
    public CartPopupPage openCartPopup() {
        WaitUtils.waitForVisible(wait, cartIcon).click();
        return new CartPopupPage(driver, wait);
    }

}