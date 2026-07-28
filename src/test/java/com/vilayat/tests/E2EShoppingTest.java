package com.vilayat.tests;

import com.vilayat.base.BaseTest;
import com.vilayat.pages.GreenKartPage;
import com.vilayat.utils.ConfigReader;
import com.vilayat.utils.TestData;

import org.openqa.selenium.support.ui.WebDriverWait;
import org.testng.Assert;
import org.testng.annotations.Test;

public class E2EShoppingTest extends BaseTest {
    @Test
    public void verifyEndToEndShoppingFlow() {
        driver.get(ConfigReader.getBaseUrl());

        GreenKartPage greenKartPage = new GreenKartPage(driver, wait);

        greenKartPage.addItemsToCart(TestData.PRODUCTS_E2E);
        greenKartPage.proceedToCheckout();
        greenKartPage.applyPromoCode(TestData.PROMO_VALID);

        String promoText = greenKartPage.getPromoInfoText();
        Assert.assertEquals(promoText, TestData.PROMO_SUCCESS_MSG);

        greenKartPage.placeOrder(TestData.COUNTRY_INDIA);
    }
}