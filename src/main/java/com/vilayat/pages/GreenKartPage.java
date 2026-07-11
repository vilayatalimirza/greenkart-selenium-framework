package com.vilayat.pages;

import java.util.Arrays;
import java.util.List;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
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
    
    public GreenKartPage(WebDriver driver, WebDriverWait wait) {
        this.driver = driver;
        this.wait = wait;
    }
    
    public void addItemsToCart(String[] items_needed) {
    	int itemfoundcount = 0;
    	List<WebElement> products = driver.findElements(productNames);
    	List<String> itemsNeededList = Arrays.asList(items_needed);
    	
    	for(int i=0;i<products.size();i++) {
    		String fn = products.get(i).getText().split("-")[0].trim();
    		
    		if (itemsNeededList.contains(fn)) {
    			itemfoundcount++;
    			driver.findElements(addToCartButtons).get(i).click();
    			
    		if (itemfoundcount == items_needed.length)
    			break;
    			
    		}
    	}
        
    }
    
    public void proceedToCheckout() {
        wait.until(ExpectedConditions.elementToBeClickable(cartIcon));
        driver.findElement(cartIcon).click();
        wait.until(ExpectedConditions.elementToBeClickable(proceedToCheckoutBtn));
        driver.findElement(proceedToCheckoutBtn).click();
    }

    public void applyPromoCode(String promoCode) {
        wait.until(ExpectedConditions.visibilityOfElementLocated(promoCodeInput));
        driver.findElement(promoCodeInput).sendKeys(promoCode);
        driver.findElement(promoApplyBtn).click();
    }

    public String getPromoInfoText() {
        wait.until(ExpectedConditions.visibilityOfElementLocated(promoInfoText));
        return driver.findElement(promoInfoText).getText();
    }

    public void placeOrder(String country) {
        driver.findElement(placeOrderBtn).click();
        Select select = new Select(driver.findElement(countryDropdown));
        select.selectByVisibleText(country);
        driver.findElement(termsCheckbox).click();
        driver.findElement(proceedFinalBtn).click();
    }
    
}
