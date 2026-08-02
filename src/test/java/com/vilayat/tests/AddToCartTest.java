package com.vilayat.tests;

import com.vilayat.base.BaseTest;
import com.vilayat.pages.GreenKartPage;
import com.vilayat.utils.ConfigReader;
import com.vilayat.utils.TestData;
import org.testng.Assert;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

public class AddToCartTest extends BaseTest {

    private GreenKartPage page;

    @BeforeMethod
    public void navigateToHomepage() {
        driver.get(ConfigReader.getBaseUrl());
        page = new GreenKartPage(driver, wait);
    }

    // --- TS-AC-001 / 003: single product increases cart count ---
    @Test
    public void verifySingleProductIncreasesCartCount() {
        String countBefore = page.getCartItemCount();
        page.addProductToCartByName("Brocolli");
        String countAfter = page.getCartItemCount();
        System.out.println("Count before: " + countBefore + " | after: " + countAfter);
        Assert.assertNotEquals(countAfter, countBefore, "Cart count should change after adding a product");
    }

    // --- TS-AC-002: button text changes after adding ---
    @Test
    public void verifyAddToCartButtonTextChangesAfterAdd() {
        String before = page.getAddToCartButtonText("Brocolli");
        page.addProductToCartByName("Brocolli");
        String after = page.getAddToCartButtonText("Brocolli");
        System.out.println("Button text before: [" + before + "] | after: [" + after + "]");
        Assert.assertNotEquals(after, before, "Button text should change after adding to cart");
    }

    // --- TS-AC-005: quantity increased to 5 before adding ---
    @Test
    public void verifyQuantityFiveIsSetBeforeAdding() {
        page.increaseQuantity("Brocolli", 4); // starts at 1, +4 = 5
        String qty = page.getQuantityValue("Brocolli");
        System.out.println("Quantity before adding: " + qty);
        Assert.assertEquals(qty, "5", "Quantity selector should show 5 before Add to Cart is clicked");
    }

    // --- TS-AC-006: two different products both added ---
    @Test
    public void verifyTwoDifferentProductsBothAdded() {
        page.addProductToCartByName("Brocolli");
        page.addProductToCartByName("Cucumber");
        String count = page.getCartItemCount();
        System.out.println("Cart count after adding 2 different products: " + count);
        Assert.assertTrue(count.contains("2"), "Cart should reflect 2 items after adding two different products");
    }

    // --- TS-AC-007: mixed quantities across products sum correctly ---
    @Test
    public void verifyCartCountEqualsTotalMixedQuantities() {
        page.increaseQuantity("Brocolli", 1); // qty 2
        page.addProductToCartByName("Brocolli");
        page.increaseQuantity("Cucumber", 2); // qty 3
        page.addProductToCartByName("Cucumber");
        String count = page.getCartItemCount();
        System.out.println("Cart count for mixed quantities (expect 2): " + count);
        Assert.assertEquals(count,"2", "Cart count should equal no. of items");
    }

    // --- TS-AC-010: every product can be added one by one ---
    @Test
    public void verifyEveryProductCanBeAdded() {
        java.util.List<String> allProducts = page.getVisibleProductNames();
        for (String product : allProducts) {
            page.addProductToCartByName(product);
        }
        String count = page.getCartItemCount();
        System.out.println("Cart count after adding all " + allProducts.size() + " products: " + count);
        Assert.assertTrue(count.contains(String.valueOf(allProducts.size())),
            "Cart count should reflect all products added");
    }

    // --- TS-AC-011: adding the same product twice (discovery) ---
    @Test
    public void verifyAddingSameProductTwice() {
        page.addProductToCartByName("Brocolli");
        page.addProductToCartByName("Brocolli");
        String count = page.getCartItemCount();
        System.out.println("Cart count after adding Brocolli twice: " + count);
        // No strict assertion yet — discovering real behavior first.
        Assert.assertNotNull(count, "Cart count should be readable, not crash, after duplicate add");
    }

    // --- TS-AC-012: add after changing quantity to 3 ---
    @Test
    public void verifyAddAfterChangingQuantityToThree() {
        page.increaseQuantity("Brocolli", 2); // 1 -> 3
        String qtyBeforeAdd = page.getQuantityValue("Brocolli");
        Assert.assertEquals(qtyBeforeAdd, "3", "Quantity should be 3 before adding");
        page.addProductToCartByName("Brocolli");
        String count = page.getCartItemCount();
        System.out.println("Cart count after adding qty 3: " + count);
    }

    // --- TS-AC-024: sequential additions update count correctly each time ---
    @Test
    public void verifySequentialAdditionsUpdateCountEachTime() {
        page.addProductToCartByName("Brocolli");
        String afterFirst = page.getCartItemCount();

        page.addProductToCartByName("Cucumber");
        String afterSecond = page.getCartItemCount();

        page.addProductToCartByName("Beetroot");
        String afterThird = page.getCartItemCount();

        System.out.println(afterFirst + " -> " + afterSecond + " -> " + afterThird);
        Assert.assertNotEquals(afterSecond, afterFirst, "Count should change after 2nd addition");
        Assert.assertNotEquals(afterThird, afterSecond, "Count should change after 3rd addition");
    }

    // --- TS-AC-026 / 027: first and last product addable ---
    @Test
    public void verifyFirstProductCanBeAdded() {
        String firstProduct = page.getVisibleProductNames().get(0);
        page.addProductToCartByName(firstProduct);
        String count = page.getCartItemCount();
        Assert.assertNotEquals(count, "0", "First product should be added successfully");
    }

    @Test
    public void verifyLastProductCanBeAdded() {
        java.util.List<String> names = page.getVisibleProductNames();
        String lastProduct = names.get(names.size() - 1);
        page.addProductToCartByName(lastProduct);
        String count = page.getCartItemCount();
        Assert.assertNotEquals(count, "0", "Last product should be added successfully");
    }

    // --- TS-AC-015 / 029: cart updates immediately, no refresh needed ---
    @Test
    public void verifyCartUpdatesImmediatelyWithoutRefresh() {
        String before = page.getCartItemCount();
        page.addProductToCartByName("Brocolli");
        String after = page.getCartItemCount(); // no driver.navigate().refresh() call
        Assert.assertNotEquals(after, before, "Cart should update immediately without a page refresh");
    }

    // --- TS-AC-036: quantity changed multiple times before adding, final value used ---
    @Test
    public void verifyFinalQuantityUsedAfterMultipleChanges() {
        page.increaseQuantity("Brocolli", 3); // 1 -> 4
        page.decreaseQuantity("Brocolli", 2); // 4 -> 2
        page.increaseQuantity("Brocolli", 1); // 2 -> 3
        String finalQty = page.getQuantityValue("Brocolli");
        System.out.println("Final quantity before add (expect 3): " + finalQty);
        Assert.assertEquals(finalQty, "3", "Final quantity after multiple changes should be 3");
    }

    // --- TS-AC-040: changing one product's quantity doesn't affect another's ---
    @Test
    public void verifyOtherProductQuantitySelectorsUnaffected() {
        page.increaseQuantity("Brocolli", 2); // Brocolli -> 3
        String cucumberQty = page.getQuantityValue("Cucumber");
        System.out.println("Cucumber quantity after changing only Brocolli's: " + cucumberQty);
        Assert.assertEquals(cucumberQty, "1", "Other products' quantity selectors should remain unaffected");
    }

    // --- TS-AC-041: cart icon remains visible after adding multiple products ---
    @Test
    public void verifyCartIconRemainsVisibleAfterMultipleAdds() {
        page.addProductToCartByName("Brocolli");
        page.addProductToCartByName("Cucumber");
        Assert.assertTrue(page.isCartIconDisplayed(), "Cart icon should remain visible after multiple additions");
    }

    // --- TS-AC-057: add to cart doesn't break search afterward ---
    @Test
    public void verifyAddToCartDoesNotBreakSearch() {
        page.addProductToCartByName("Brocolli");
        page.searchProduct(TestData.SEARCH_PARTIAL);
        java.util.List<String> results = page.getVisibleProductNames();
        Assert.assertFalse(results.isEmpty(), "Search should still work normally after adding to cart");
    }
}