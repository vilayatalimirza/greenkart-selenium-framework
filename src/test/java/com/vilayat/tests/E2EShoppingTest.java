package com.vilayat.tests;

import com.vilayat.base.BaseTest;
import com.vilayat.pages.GreenKartPage;
import org.testng.Assert;
import org.testng.annotations.Test;

public class E2EShoppingTest extends BaseTest {

    @Test
    public void verifyEndToEndShoppingFlow() {
        // 1. Open Application
        driver.get("https://rahulshettyacademy.com/seleniumPractise/");

        // 2. Initialize Page Object
        GreenKartPage greenKartPage = new GreenKartPage(driver, wait);

        // 3. Add items to cart
        String[] itemsNeeded = {"Cucumber", "Brocolli", "Beetroot"};
        greenKartPage.addItemsToCart(itemsNeeded);

        // 4. Checkout
        greenKartPage.proceedToCheckout();

        // 5. Apply promo code
        greenKartPage.applyPromoCode("rahulshettyacademy");

        // 6. Assert promo applied
        String promoText = greenKartPage.getPromoInfoText();
        Assert.assertEquals(promoText, "Code applied ..!");

        // 7. Place order
        greenKartPage.placeOrder("India");
    }
}