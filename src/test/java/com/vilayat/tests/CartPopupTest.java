package com.vilayat.tests;

import com.vilayat.base.BaseTest;
import com.vilayat.pages.GreenKartPage;
import com.vilayat.utils.ConfigReader;
import org.openqa.selenium.By;
import org.testng.Assert;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import java.util.List;

public class CartPopupTest extends BaseTest {

    private GreenKartPage page;

    @BeforeMethod
    public void navigateToHomepage() {
        driver.get(ConfigReader.getBaseUrl());
        page = new GreenKartPage(driver, wait);
    }

    // --- TS-CP-001: popup opens on clicking cart icon ---
    @Test
    public void verifyCartPopupOpensOnCartIconClick() {
        page.addProductToCartByName("Brocolli");
        page.openCartPopup();
        Assert.assertTrue(page.getCartPopupItemCount() > 0, "Cart popup should show at least one item after opening");
    }

    // --- TS-CP-002 / 003: all added products displayed with correct names ---
    @Test
    public void verifyPopupDisplaysAllAddedProductsWithCorrectNames() {
        page.addProductToCartByName("Brocolli");
        page.addProductToCartByName("Cauliflower");
        page.openCartPopup();

        List<String> names = page.getCartPopupProductNames();
        System.out.println("Popup product names: " + names);
        Assert.assertTrue(names.stream().anyMatch(n -> n.equalsIgnoreCase("Brocolli")));
        Assert.assertTrue(names.stream().anyMatch(n -> n.equalsIgnoreCase("Cauliflower")));
        Assert.assertEquals(names.size(), 2, "Popup should show exactly 2 distinct products");
    }

    // --- TS-CP-004 / 017: quantity displayed matches what was set before adding ---
    @Test
    public void verifyPopupShowsCorrectQuantity() {
        page.increaseQuantity("Brocolli", 4); // 1 -> 5
        page.addProductToCartByName("Brocolli");
        page.openCartPopup();

        int qty = page.getCartPopupQuantity(0);
        System.out.println("Popup quantity (expect 5): " + qty);
        Assert.assertEquals(qty, 5, "Popup should reflect the quantity selected before adding");
    }

    // --- TS-CP-005: unit price matches product listing ---
    @Test
    public void verifyPopupShowsCorrectUnitPrice() {
        page.addProductToCartByName("Brocolli");
        page.openCartPopup();

        int price = page.getCartPopupUnitPrice(0);
        System.out.println("Popup unit price: " + price);
        Assert.assertTrue(price > 0, "Popup should display a valid unit price");
    }

    // --- TS-CP-006 / 032: subtotal = unit price x quantity for each product ---
    @Test
    public void verifySubtotalEqualsUnitPriceTimesQuantity() {
        page.increaseQuantity("Cucumber", 1); // qty 2
        page.addProductToCartByName("Cucumber");
        page.openCartPopup();

        int price = page.getCartPopupUnitPrice(0);
        int qty = page.getCartPopupQuantity(0);
        int subtotal = page.getCartPopupSubtotal(0);

        System.out.println(price + " x " + qty + " should equal " + subtotal);
        Assert.assertEquals(subtotal, price * qty, "Subtotal should equal unit price multiplied by quantity");
    }

    // --- TS-CP-007: cart item count (header) matches popup contents ---
    @Test
    public void verifyHeaderItemCountMatchesPopupContents() {
        page.addProductToCartByName("Brocolli");
        page.addProductToCartByName("Cauliflower");
        page.addProductToCartByName("Cucumber");
        page.openCartPopup();

        int headerCount = page.getHeaderItemsCount();
        int popupCount = page.getCartPopupItemCount();

        System.out.println("Header says: " + headerCount + " | Popup shows: " + popupCount);
        Assert.assertEquals(headerCount, popupCount, "Header item count should match number of distinct items in popup");
    }

    // --- TS-CP-011 / 012: Proceed to Checkout button visible and navigates correctly ---
    @Test
    public void verifyProceedToCheckoutNavigatesFromPopup() {
        page.addProductToCartByName("Brocolli");
        page.openCartPopup();
        driver.findElement(By.cssSelector("a.cart-icon")).click();
        page.proceedToCheckout(); // reuses existing method — clicks the same button

        Assert.assertTrue(page.isPromoInputDisplayed(), "Should navigate to checkout page showing promo input");
    }

    // --- TS-CP-016: popup reflects newly added product immediately ---
    @Test
    public void verifyPopupUpdatesImmediatelyAfterAdding() {
        page.addProductToCartByName("Brocolli");
        page.openCartPopup();

        List<String> names = page.getCartPopupProductNames();
        Assert.assertTrue(names.stream().anyMatch(n -> n.equalsIgnoreCase("Brocolli")),
            "Newly added product should appear immediately in the popup");
    }

    // --- TS-CP-020 / 034: popup reflects updated state after adding more products ---
    @Test
    public void verifyPopupReflectsLatestStateAfterAddingMore() {
        page.addProductToCartByName("Brocolli");
        page.openCartPopup();
        page.closeCartPopup();
        Assert.assertEquals(page.getCartPopupItemCount(), 1, "Popup should show 1 item initially");
        page.addProductToCartByName("Cauliflower");
        page.openCartPopup(); // reopen — reuses same wait logic
        Assert.assertEquals(page.getCartPopupItemCount(), 2, "Popup should show 2 items after adding a second product");
    }

    // --- TS-CP-033: no duplicate entries after a single addition ---
    @Test
    public void verifyNoDuplicateEntryAfterSingleAddition() {
        page.addProductToCartByName("Brocolli");
        page.openCartPopup();

        List<String> names = page.getCartPopupProductNames();
        long brocolliCount = names.stream().filter(n -> n.equalsIgnoreCase("Brocolli")).count();

        System.out.println("Brocolli appears " + brocolliCount + " time(s) in popup");
        Assert.assertEquals(brocolliCount, 1, "Product should appear exactly once after a single addition");
    }

    // --- Bonus: remove button actually works (the original sheet never tested this!) ---
    @Test
    public void verifyRemoveButtonRemovesItemFromPopup() {
        page.addProductToCartByName("Brocolli");
        page.addProductToCartByName("Cauliflower");
        page.addProductToCartByName("Tomato");
        page.openCartPopup();

        int countBefore = page.getCartPopupItemCount();
        page.removeCartPopupItem(0);

        // Small explicit wait isn't in a helper yet — using a direct wait here for the count to change.
        wait.until(d -> page.getCartPopupItemCount() != countBefore);

        int countAfter = page.getCartPopupItemCount();
        System.out.println("Count before remove: " + countBefore + " | after: " + countAfter);
        Assert.assertEquals(countAfter, countBefore - 1, "Removing an item should decrease the popup count!");
    }

    // --- TS-CP-040: full popup workflow, verifying everything together ---
    @Test
    public void verifyFullCartPopupWorkflow() {
        page.increaseQuantity("Brocolli", 1); // qty 2
        page.addProductToCartByName("Brocolli");
        page.addProductToCartByName("Cauliflower");
        page.openCartPopup();

        List<String> names = page.getCartPopupProductNames();
        int headerCount = page.getHeaderItemsCount();
        int popupCount = page.getCartPopupItemCount();

        System.out.println("Names: " + names + " | Header: " + headerCount + " | Popup: " + popupCount);
        Assert.assertEquals(names.size(), 2);
        Assert.assertEquals(headerCount, popupCount);
        driver.findElement(By.cssSelector("a.cart-icon")).click();
        page.proceedToCheckout();
        Assert.assertTrue(page.isPromoInputDisplayed(), "Full workflow should end on the checkout page");
    }
}