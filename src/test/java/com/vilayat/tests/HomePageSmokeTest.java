package com.vilayat.tests;

import com.vilayat.base.BaseTest;
import com.vilayat.pages.GreenKartPage;
import com.vilayat.utils.ConfigReader;
import org.testng.Assert;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

public class HomePageSmokeTest extends BaseTest {

    private GreenKartPage greenKartPage;

    @BeforeMethod
    public void navigateToHomepage() {
        driver.get(ConfigReader.getBaseUrl());
        greenKartPage = new GreenKartPage(driver, wait);
    }

    @Test
    public void verifyHomepageTitleIsNotEmpty() {
        Assert.assertFalse(greenKartPage.getPageTitle().isEmpty(), "Page title should not be empty");
    }

    @Test
    public void verifyCartIconIsDisplayed() {
        Assert.assertTrue(greenKartPage.isCartIconDisplayed(), "Cart icon should be visible on load");
    }

    @Test
    public void verifyCartItemCountIsZeroInitially() {
        Assert.assertTrue(greenKartPage.getCartItemCount().contains("0"), "Cart should show 0 items initially");
    }

    @Test
    public void verifyCartTotalIsZeroInitially() {
        Assert.assertTrue(greenKartPage.getHomepageCartTotal().contains("0"), "Cart total should be 0 initially");
    }

    @Test
    public void verifyProductsAreDisplayed() {
        Assert.assertTrue(greenKartPage.getProductCount() > 0, "At least one product should be displayed");
    }

    @Test
    public void verifyAddToCartButtonCountMatchesProductCount() {
        Assert.assertEquals(greenKartPage.getAddToCartButtonCount(), greenKartPage.getProductCount(),
            "Every product should have exactly one Add to Cart button");
    }

    @Test
    public void verifySearchBoxIsEnabled() {
        Assert.assertTrue(greenKartPage.isSearchBoxEnabled(), "Search box should be enabled");
    }

    @Test
    public void verifyTopDealsLinkIsDisplayed() {
        Assert.assertTrue(greenKartPage.isTopDealsLinkDisplayed(), "Top Deals link should be visible");
    }

    @Test
    public void verifyFlightBookingLinkIsDisplayed() {
        Assert.assertTrue(greenKartPage.isFlightBookingLinkDisplayed(), "Flight Booking link should be visible");
    }

    @Test
    public void verifyHomepageReloadsSuccessfully() {
        driver.navigate().refresh();
        Assert.assertTrue(greenKartPage.isLogoDisplayed(), "Logo should still be visible after refresh");
    }
}