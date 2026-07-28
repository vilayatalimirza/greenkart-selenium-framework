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
}