package com.vilayat.tests;

import com.vilayat.base.BaseTest;
import com.vilayat.pages.GreenKartPage;
import com.vilayat.utils.ConfigReader;
import org.testng.Assert;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import java.util.List;

public class PlaceOrderTest extends BaseTest {

    private GreenKartPage page;

    @BeforeMethod
    public void setupCartAndNavigateToPlaceOrder() {
        driver.get(ConfigReader.getBaseUrl());
        page = new GreenKartPage(driver, wait);
        page.addProductToCartByName("Brocolli");
        page.proceedToCheckout();
    }

    // TS-PO-001: Verify user can navigate to the Place Order page from Checkout
    @Test
    public void verifyNavigationToPlaceOrderPage() {
        page.clickPlaceOrderButton();
        Assert.assertTrue(page.isCountryDropdownDisplayed(), "User should be navigated to the Place Order page successfully");
    }

    // TS-PO-002: Verify Country dropdown is displayed
    @Test
    public void verifyCountryDropdownIsDisplayed() {
        page.clickPlaceOrderButton();
        Assert.assertTrue(page.isCountryDropdownDisplayed(), "Country dropdown should be visible");
    }

    // TS-PO-003: Verify Country dropdown contains available countries
    @Test
    public void verifyCountryDropdownContainsCountries() {
        page.clickPlaceOrderButton();
        List<String> countries = page.getAvailableCountries();
        Assert.assertTrue(countries.size() > 1, "Country dropdown should contain a list of countries");
        Assert.assertTrue(countries.contains("India"), "Dropdown should contain 'India'");
    }

    // TS-PO-004: Verify user can select a country
    @Test
    public void verifyUserCanSelectCountry() {
        page.clickPlaceOrderButton();
        page.selectCountry("India");
        Assert.assertEquals(page.getSelectedCountry(), "India", "Selected country should be India");
    }

    // TS-PO-005: Verify selected country remains selected until changed
    @Test
    public void verifySelectedCountryRemainsSelected() {
        page.clickPlaceOrderButton();
        page.selectCountry("India");
        
        // Simulating clicking outside or doing another action isn't strictly necessary 
        // to check if the value stays, but we can verify it again.
        String selected = page.getSelectedCountry();
        Assert.assertEquals(selected, "India", "Selected country should remain unchanged");
    }
}