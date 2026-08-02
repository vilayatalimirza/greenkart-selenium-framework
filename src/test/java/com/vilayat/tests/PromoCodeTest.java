package com.vilayat.tests;

import com.vilayat.base.BaseTest;
import com.vilayat.pages.GreenKartPage;
import com.vilayat.utils.ConfigReader;
import com.vilayat.utils.TestData;

import org.testng.Assert;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

public class PromoCodeTest extends BaseTest {

    @DataProvider(name = "promoCodeData")
    public Object[][] promoCodeData() {
        return new Object[][] {
            { TestData.PROMO_VALID, TestData.PROMO_SUCCESS_MSG },
            { TestData.PROMO_INVALID, TestData.PROMO_ERROR_MSG }
        };
    }

    @Test(dataProvider = "promoCodeData")
    public void verifyPromoCodeBehavior(String promoCode, String expectedMessage) {
        driver.get(ConfigReader.getBaseUrl());

        GreenKartPage greenKartPage = new GreenKartPage(driver, wait);
        greenKartPage.addItemsToCart(TestData.PRODUCTS_E2E);
        greenKartPage.proceedToCheckout();
        greenKartPage.applyPromoCode(promoCode);

        String actualMessage = greenKartPage.getPromoInfoText();
        Assert.assertEquals(actualMessage, expectedMessage);
    }
 // --- TS-PC-001 / 002: simple UI checks ---
    @Test
    public void verifyPromoInputAndApplyButtonAreUsable() {
        driver.get(ConfigReader.getBaseUrl());
        GreenKartPage page = new GreenKartPage(driver, wait);
        page.addItemsToCart(TestData.PRODUCTS_E2E);
        page.proceedToCheckout();

        Assert.assertTrue(page.isPromoInputDisplayed(), "Promo input should be visible on checkout");
        Assert.assertTrue(page.isApplyButtonEnabled(), "Apply button should be enabled");
    }

    // --- Edge case validation codes: we genuinely don't know these outcomes yet ---
    @DataProvider(name = "promoEdgeCaseData")
    public Object[][] promoEdgeCaseData() {
        return new Object[][] {
            { TestData.PROMO_EMPTY },
            { TestData.PROMO_LEADING_SPACES },
            { TestData.PROMO_TRAILING_SPACES },
            { TestData.PROMO_MIXED_CASE },
            { TestData.PROMO_SPECIAL_CHARS },
            { TestData.PROMO_NUMERIC },
            { TestData.PROMO_INVALID_ALPHANUMERIC },
            { TestData.PROMO_LONG_STRING }
        };
    }

    @Test(dataProvider = "promoEdgeCaseData")
    public void verifyPromoEdgeCaseHandledSafely(String promoCode) {
        driver.get(ConfigReader.getBaseUrl());
        GreenKartPage page = new GreenKartPage(driver, wait);
        page.addItemsToCart(TestData.PRODUCTS_E2E);
        page.proceedToCheckout();
        page.applyPromoCode(promoCode);

        String actualMessage = page.getPromoInfoText();
        System.out.println("Promo code [" + promoCode + "] -> message: " + actualMessage);

        // Honest, discovery-level assertion: it must be ONE of the two known messages, not a crash.
        Assert.assertTrue(
        	    actualMessage.equals(TestData.PROMO_SUCCESS_MSG)|| actualMessage.equals(TestData.PROMO_ERROR_MSG)
        	        || actualMessage.equals(TestData.PROMO_EMPTY_MSG),
        	    "Unexpected message for promo code [" + promoCode + "]: " + actualMessage
        	);
    }

    // --- TS-PC-004: total amount actually changes ---
    @Test
    public void verifyTotalAmountDecreasesAfterValidPromo() {
        driver.get(ConfigReader.getBaseUrl());
        GreenKartPage page = new GreenKartPage(driver, wait);
        page.addItemsToCart(TestData.PRODUCTS_E2E);
        page.proceedToCheckout();

        String totalBefore = page.getCheckoutTotalBeforeDiscount();
        page.applyPromoCode(TestData.PROMO_VALID);
        String totalAfter = page.getCheckoutTotalAfterDiscount();
        System.out.println("Total before: " + totalBefore + " | Total after: " + totalAfter);
        Assert.assertNotEquals(totalAfter, totalBefore, "Discounted total should differ from the original subtotal");
    }
    // --- TS-PC-014: rapid repeated Apply clicks (exploratory) ---
    @Test
    public void verifyRapidMultipleApplyClicksDoesNotCrash() {
        driver.get(ConfigReader.getBaseUrl());
        GreenKartPage page = new GreenKartPage(driver, wait);
        page.addItemsToCart(TestData.PRODUCTS_E2E);
        page.proceedToCheckout();

        page.applyPromoCode(TestData.PROMO_VALID);
        page.applyPromoCode(TestData.PROMO_VALID); // apply again on top
        String message = page.getPromoInfoText();

        System.out.println("Message after repeated apply: " + message);
        Assert.assertNotNull(message, "Repeated apply clicks should not crash the page");
        
    }

    // --- TS-PC-018: field value after invalid attempt (exploratory) ---
    @Test
    public void verifyFieldValueAfterInvalidPromo() {
        driver.get(ConfigReader.getBaseUrl());
        GreenKartPage page = new GreenKartPage(driver, wait);
        page.addItemsToCart(TestData.PRODUCTS_E2E);
        page.proceedToCheckout();
        page.applyPromoCode(TestData.PROMO_INVALID);

        String fieldValue = page.getPromoInputValue();
        System.out.println("Promo field value after invalid attempt: [" + fieldValue + "]");
        // No hard assertion yet — we're discovering real behavior first.
    }

    // --- TS-PC-019: promo after page refresh (we suspect this might reset the cart entirely) ---
    @Test
    public void verifyPromoCodeAfterPageRefresh() {
        driver.get(ConfigReader.getBaseUrl());
        GreenKartPage page = new GreenKartPage(driver, wait);
        page.addItemsToCart(TestData.PRODUCTS_E2E);
        page.proceedToCheckout();

        driver.navigate().refresh();

        // Honest possibility: refreshing a client-side app like this may reset cart state entirely.
        // This assertion just confirms the page still loads without crashing — we'll learn the real behavior from output.
        Assert.assertTrue(page.isLogoDisplayed(), "Page should still load successfully after refresh");
    }

    // --- TS-PC-020: full workflow ---
    @Test
    public void verifyFullPromoCodeWorkflow() {
        driver.get(ConfigReader.getBaseUrl());
        GreenKartPage page = new GreenKartPage(driver, wait);
        page.addItemsToCart(TestData.PRODUCTS_E2E);
        page.proceedToCheckout();
        page.applyPromoCode(TestData.PROMO_INVALID);
        Assert.assertEquals(page.getPromoInfoText(), TestData.PROMO_ERROR_MSG);
        String totalBefore = page.getCheckoutTotalBeforeDiscount();
        page.applyPromoCode(TestData.PROMO_VALID);
        Assert.assertEquals(page.getPromoInfoText(), TestData.PROMO_SUCCESS_MSG);
        String totalAfter= page.getCheckoutTotalAfterDiscount();
        System.out.println("Original: " + totalBefore + " | Discounted: " + totalAfter);
        Assert.assertNotEquals(totalAfter, totalBefore, "Discounted total should differ from the original subtotal");
    }
    
}