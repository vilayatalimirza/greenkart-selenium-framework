package com.vilayat.pages;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.StaleElementReferenceException;
import org.openqa.selenium.TimeoutException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import com.vilayat.utils.WaitUtils;
import com.vilayat.exceptions.ProductNotFoundException;

import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.Select;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.openqa.selenium.Keys;



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
    private By flightBookingLink = By.linkText("Flight Booking");
    private By cartCount = By.cssSelector(".cart-info tr:nth-child(1) strong");
    private By cartTotal = By.cssSelector(".cart-info tr:nth-child(2) strong"); 
    private By checkoutCartTotalBeforeDiscount = By.cssSelector(".totAmt");
    private By checkoutCartTotalAfterDiscount = By.cssSelector(".discountAmt");
    private By incrementButtons = By.cssSelector("a.increment");
    private By decrementButtons = By.cssSelector("a.decrement");
    private By quantityInputs = By.cssSelector("input.quantity");
    private By checkoutProductNames = By.cssSelector("#productCartTables p.product-name");
    private By checkoutQuantities = By.cssSelector("#productCartTables p.quantity");
    private By checkoutAmounts = By.cssSelector("#productCartTables p.amount"); // 2 per row: [price, total, price, total...]
    private By checkoutSummaryBlock = By.xpath("//div[contains(@style,'text-align: right')]");
    private By discount = By.cssSelector(".discountPerc");
    private By cartPopupItems = By.cssSelector("li.cart-item");
    private By cartItemNames = By.cssSelector("li.cart-item p.product-name");
    private By cartItemPrices = By.cssSelector("li.cart-item p.product-price");
    private By cartItemQuantities = By.cssSelector("li.cart-item p.quantity");
    private By cartItemAmounts = By.cssSelector("li.cart-item p.amount");
    private By cartItemRemoveButtons = By.cssSelector("a.product-remove");

    
    
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
        WebElement input = driver.findElement(promoCodeInput);
        String currentValue = input.getDomProperty("value");
        if (currentValue != null) {
            for (int i = 0; i < currentValue.length(); i++) {
                input.sendKeys(Keys.BACK_SPACE);
            }
        }
        // Capture old message text (may be empty on first-ever call) before clicking Apply.
        String oldMessage = "";
        try {
            oldMessage = driver.findElement(promoInfoText).getText();
        } catch (Exception e) {
            // No message present yet — fine, this is the first attempt.
        }
        input.sendKeys(promoCode);
        WaitUtils.waitForClickable(wait, promoApplyBtn);
        driver.findElement(promoApplyBtn).click();
        final String finalOldMessage = oldMessage;
        try {
            wait.until(d -> !driver.findElement(promoInfoText).getText().equals(finalOldMessage));
        } catch (org.openqa.selenium.TimeoutException e) {
            // Message may legitimately stay the same on a repeat apply — not a failure by itself.
        }
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
    	WaitUtils.waitForVisible(wait, productNames);
        // 1. Grab a reference to the first product on the screen BEFORE we search
        WebElement firstProductBeforeSearch = driver.findElements(this.productNames).get(0);
        
        // 2. Perform the search
        driver.findElement(searchBox).clear();
        driver.findElement(searchBox).sendKeys(productName);
        
        // 3. Wait for the UI to re-render the list. 
        // We know it's done rendering when that old first element detaches from the DOM (goes stale).
        try {
            wait.until(ExpectedConditions.stalenessOf(firstProductBeforeSearch));
        } catch (Exception e) {
            // If it times out or throws an error, the DOM might have been so fast it already updated. 
            // We safely swallow this so the test can continue.
        }
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

    public String getCheckoutTotalBeforeDiscount() {
        WaitUtils.waitForVisible(wait, checkoutCartTotalBeforeDiscount);
        return driver.findElement(checkoutCartTotalBeforeDiscount).getText();
    }

    public String getCheckoutTotalAfterDiscount() {
        WaitUtils.waitForVisible(wait, checkoutCartTotalAfterDiscount);
        return driver.findElement(checkoutCartTotalAfterDiscount).getText();
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
    public boolean isPromoInputDisplayed() {
    	WaitUtils.waitForVisible(wait, promoCodeInput);
        return driver.findElement(promoCodeInput).isDisplayed();
    }

    public boolean isApplyButtonEnabled() {
        return driver.findElement(promoApplyBtn).isEnabled();
    }

    public String getPromoInputValue() {
        return driver.findElement(promoCodeInput).getDomProperty("value");
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


	public java.util.List<String> getCheckoutProductNames() {
	    WaitUtils.waitForVisible(wait, checkoutProductNames);
	    java.util.List<String> names = new java.util.ArrayList<>();
	    for (WebElement e : driver.findElements(checkoutProductNames)) {
	    	if(!e.getText().isBlank())
	        	names.add(e.getText().split("-")[0].trim());
	    }
	    return names;
	}
	
	public int getCheckoutQuantity(int rowIndex) {
		WaitUtils.waitForVisible(wait, checkoutQuantities);
	    return parseNumber(driver.findElements(checkoutQuantities).get(rowIndex).getText().trim());
	}
	
	public int getCheckoutPrice(int rowIndex) {
		WaitUtils.waitForVisible(wait, checkoutAmounts);
	    // Price is always the FIRST .amount in each row's pair
	    return parseNumber(driver.findElements(checkoutAmounts).get(rowIndex * 2).getText().trim());
	}
	
	public int getCheckoutRowTotal(int rowIndex) {
		WaitUtils.waitForVisible(wait, checkoutAmounts);
	    // Total is always the SECOND .amount in each row's pair
	    return parseNumber(driver.findElements(checkoutAmounts).get(rowIndex * 2 + 1).getText().trim());
	}
	
	public int getCheckoutRowCount() {
		WaitUtils.waitForVisible(wait, checkoutProductNames);
	    return driver.findElements(checkoutProductNames).size();
	}
	
	public int getNoOfItemsFromSummary() {
		WaitUtils.waitForVisible(wait, checkoutSummaryBlock);
	    String fullText = driver.findElement(checkoutSummaryBlock).getText();
	    for (String line : fullText.split("\n")) {
	        if (line.contains("No. of Items")) {
	            String numberPart = line.split(":")[1].trim();
	            return parseNumber(numberPart);
	        }
	    }
    throw new RuntimeException("Could not find 'No. of Items' in summary block: " + fullText);
	}
	
	public String getDiscountPercentage() {
        WaitUtils.waitForVisible(wait, discount);
        return driver.findElement(discount).getText();
    }

    public boolean isPlaceOrderButtonDisplayed() {
        WaitUtils.waitForVisible(wait, placeOrderBtn);
        return driver.findElement(placeOrderBtn).isDisplayed();
    }

    public boolean isPlaceOrderButtonEnabled() {
        return driver.findElement(placeOrderBtn).isEnabled();
    }
    private int parseNumber(String rawText) {
        String digitsOnly = rawText.replaceAll("[^0-9]", "");
        return Integer.parseInt(digitsOnly);
    }
    public void openCartPopup() {
    	WaitUtils.waitForVisible(wait, cartIcon);
        driver.findElement(cartIcon).click();
        WaitUtils.waitForVisible(wait, cartPopupItems);
    }
    
    public void closeCartPopup() {
        driver.findElement(cartIcon).click();
        WaitUtils.waitForVisible(wait, productNames);
    }
    public int getCartPopupItemCount() {
        return driver.findElements(cartPopupItems).size()/2;
    }
    public List<String> getCartPopupProductNames() {
        List<String> names = new ArrayList<>();
        List<WebElement> e = driver.findElements(cartItemNames);
        for (int i=0;i< e.size()/2;i++) {
            names.add(e.get(i).getText().split("-")[0].trim());
        }
        return names;
    }
    public int getCartPopupQuantity(int index) {
        return parseNumber(driver.findElements(cartItemQuantities).get(index).getText());
    }

    public int getCartPopupUnitPrice(int index) {
        return parseNumber(driver.findElements(cartItemPrices).get(index).getText());
    }

    public int getCartPopupSubtotal(int index) {
        return parseNumber(driver.findElements(cartItemAmounts).get(index).getText());
    }

    public void removeCartPopupItem(int index) {
        driver.findElements(cartItemRemoveButtons).get(index).click();
    }
    
    public int getHeaderItemsCount() {
        WaitUtils.waitForVisible(wait, cartCount);
        return parseNumber(driver.findElement(cartCount).getText());
    }
    public void clickPlaceOrderButton() {
        WaitUtils.waitForClickable(wait, placeOrderBtn);
        driver.findElement(placeOrderBtn).click();
    }

    public boolean isCountryDropdownDisplayed() {
        WaitUtils.waitForVisible(wait, countryDropdown);
        return driver.findElement(countryDropdown).isDisplayed();
    }

    public void selectCountry(String country) {
        WaitUtils.waitForVisible(wait, countryDropdown);
        Select select = new Select(driver.findElement(countryDropdown));
        select.selectByVisibleText(country);
    }

    public String getSelectedCountry() {
        WaitUtils.waitForVisible(wait, countryDropdown);
        Select select = new Select(driver.findElement(countryDropdown));
        return select.getFirstSelectedOption().getText();
    }

    public List<String> getAvailableCountries() {
        WaitUtils.waitForVisible(wait, countryDropdown);
        Select select = new Select(driver.findElement(countryDropdown));
        List<String> countries = new ArrayList<>();
        for (WebElement option : select.getOptions()) {
            countries.add(option.getText());
        }
        return countries;
    }
    
}
