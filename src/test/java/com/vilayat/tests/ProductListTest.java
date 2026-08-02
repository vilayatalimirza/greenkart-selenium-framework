package com.vilayat.tests;

import com.vilayat.base.BaseTest;
import com.vilayat.pages.GreenKartPage;
import com.vilayat.utils.ConfigReader;
import org.testng.Assert;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import java.util.List;

public class ProductListTest extends BaseTest {

    private GreenKartPage page;

    @BeforeMethod
    public void navigateToHomepage() {
        driver.get(ConfigReader.getBaseUrl());
        page = new GreenKartPage(driver, wait);
    }

    // TS-PL-001: Verify all products are displayed
    @Test
    public void verifyAllProductsAreDisplayed() {
        int productCount = page.getProductCount();
        Assert.assertTrue(productCount > 0, "Products should be displayed on the homepage");
        Assert.assertEquals(productCount, 30, "There should be 30 products displayed by default");
    }

    // TS-PL-002: Verify each product displays a name
    @Test
    public void verifyEachProductDisplaysAName() {
        List<String> productNames = page.getVisibleProductNames();
        Assert.assertEquals(productNames.size(), page.getProductCount(), "Each product should have a visible name");
        for (String name : productNames) {
            Assert.assertFalse(name.trim().isEmpty(), "Product name should not be empty");
        }
    }

    // TS-PL-004: Verify each product displays a price / Add to cart button
    // (Testing Add to cart button presence as proxy for product display components)
    @Test
    public void verifyEachProductDisplaysAddToCartButton() {
        int buttonCount = page.getAddToCartButtonCount();
        Assert.assertEquals(buttonCount, page.getProductCount(), "Each product should have an Add to Cart button");
    }
}