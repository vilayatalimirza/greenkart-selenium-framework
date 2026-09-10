package com.vilayat.tests;

import com.vilayat.base.BaseTest;
import com.vilayat.pages.GreenKartPage;
import com.vilayat.utils.ConfigReader;
import com.vilayat.utils.TestData;
import org.testng.Assert;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import java.util.List;

public class SearchEdgeCasesTest extends BaseTest {

    private GreenKartPage greenKartPage;

    @BeforeMethod
    public void navigateToHomepage() {
        driver.get(ConfigReader.getBaseUrl());
        greenKartPage = new GreenKartPage(driver, wait);
        greenKartPage.waitForPageLoad();
    }

    @Test
    public void verifyPartialNameSearchShowsMatchingProduct() {
        String partialQuery = "ca"; // or your target partial string
        greenKartPage.searchProduct(partialQuery);

        List<String> visibleProducts = greenKartPage.getVisibleProductNames();
        Assert.assertFalse(visibleProducts.isEmpty(), "No products returned for partial query: " + partialQuery);

        for (String product : visibleProducts) {
            boolean matches = product.toLowerCase().contains(partialQuery.toLowerCase());
            Assert.assertTrue(matches, 
                "Unexpected product in partial search results: " + product + " (Searched for: " + partialQuery + ")");
        }
    }

    @Test
    public void verifySearchIsCaseInsensitive() {
        greenKartPage.searchProduct(TestData.SEARCH_LOWERCASE);
        List<String> lowerResults = greenKartPage.getVisibleProductNames();

        greenKartPage.searchProduct(TestData.SEARCH_UPPERCASE);
        List<String> upperResults = greenKartPage.getVisibleProductNames();

        Assert.assertEquals(lowerResults, upperResults,
            "Lowercase and uppercase search should return the same results");
    }

    @Test
    public void verifyNonExistingProductShowsNoResults() {
        greenKartPage.searchProduct(TestData.SEARCH_NON_EXISTING);
        List<String> results = greenKartPage.getVisibleProductNames();

        Assert.assertTrue(results.isEmpty(), "Searching a non-existing product should return zero results");
    }

    @Test
    public void verifySpecialCharactersDoNotCrashSearch() {
        greenKartPage.searchProduct(TestData.SEARCH_SPECIAL_CHARS);
        List<String> results = greenKartPage.getVisibleProductNames();

        Assert.assertTrue(results.isEmpty(), "Special characters should return zero results, not crash the page");
    }

    @Test
    public void verifyNumericSearchShowsNoResults() {
        greenKartPage.searchProduct(TestData.SEARCH_NUMERIC);
        List<String> results = greenKartPage.getVisibleProductNames();

        Assert.assertTrue(results.isEmpty(), "Numeric search should return zero results");
    }

    @Test
    public void verifyClearingSearchRestoresFullProductList() {
        int fullCount = greenKartPage.getVisibleProductNames().size();

        greenKartPage.searchProduct(TestData.SEARCH_PARTIAL);
        Assert.assertTrue(greenKartPage.getVisibleProductNames().size() < fullCount,
            "Search should narrow the list before we clear it");

        greenKartPage.clearSearchBox(); // clear the box
        int restoredCount = greenKartPage.getVisibleProductNames().size();

        Assert.assertEquals(restoredCount, fullCount, "Clearing search should restore the full product list");
    }

    @Test
    public void verifySingleCharacterSearchBehavior() {
        // Honest note: we don't yet know if a single character filters or shows everything.
        // This assertion just proves it doesn't crash; update once we see the real result.
        greenKartPage.searchProduct(TestData.SEARCH_SINGLE_CHAR);
        List<String> results = greenKartPage.getVisibleProductNames();

        System.out.println("Single character search returned: " + results);
        Assert.assertNotNull(results, "Search should return a list, not null, even for a single character");
    }

    @Test
    public void verifyTrailingSpacesAreTrimmed() {
        greenKartPage.searchProduct(TestData.SEARCH_VALID_FULL + " ");
        List<String> results = greenKartPage.getVisibleProductNames();

        Assert.assertFalse(results.isEmpty(), "Trailing spaces should not prevent a valid match");
        Assert.assertTrue(results.get(0).toLowerCase().contains(TestData.SEARCH_VALID_FULL.toLowerCase()));
    }
}