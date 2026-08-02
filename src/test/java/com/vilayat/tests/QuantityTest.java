package com.vilayat.tests;

import com.vilayat.base.BaseTest;
import com.vilayat.pages.GreenKartPage;
import com.vilayat.utils.ConfigReader;
import org.testng.Assert;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

public class QuantityTest extends BaseTest {

    private GreenKartPage page;

    @BeforeMethod
    public void navigateToHomepage() {
        driver.get(ConfigReader.getBaseUrl());
        page = new GreenKartPage(driver, wait);
    }

    // TS-QT-001: Verify default product quantity
    @Test
    public void verifyDefaultProductQuantity() {
        String defaultQty = page.getQuantityValue("Brocolli");
        Assert.assertEquals(defaultQty, "1", "Default quantity should be 1");
    }

    // TS-QT-002: Verify quantity increments by one
    @Test
    public void verifyQuantityIncrementsByOne() {
        page.increaseQuantity("Brocolli", 1);
        String qty = page.getQuantityValue("Brocolli");
        Assert.assertEquals(qty, "2", "Quantity should increase from 1 to 2");
    }

    // TS-QT-003: Verify quantity decrements by one
    @Test
    public void verifyQuantityDecrementsByOne() {
        page.increaseQuantity("Brocolli", 1); // Now 2
        page.decreaseQuantity("Brocolli", 1); // Now 1
        String qty = page.getQuantityValue("Brocolli");
        Assert.assertEquals(qty, "1", "Quantity should decrease from 2 to 1");
    }

    // TS-QT-004: Verify quantity cannot be decreased below one
    @Test
    public void verifyQuantityCannotBeDecreasedBelowOne() {
        page.decreaseQuantity("Brocolli", 1); // Try to decrease from 1
        String qty = page.getQuantityValue("Brocolli");
        Assert.assertEquals(qty, "1", "Quantity should not decrease below 1");
    }

    // TS-QT-005: Verify multiple quantity increments
    @Test
    public void verifyMultipleQuantityIncrements() {
        page.increaseQuantity("Brocolli", 5); // 1 + 5 = 6
        String qty = page.getQuantityValue("Brocolli");
        Assert.assertEquals(qty, "6", "Quantity should increase to 6 after 5 clicks");
    }
}