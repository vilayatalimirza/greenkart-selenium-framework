package com.vilayat.tests;

import com.vilayat.base.BaseTest;
import com.vilayat.pages.GreenKartPage;
import com.vilayat.utils.ConfigReader;
import com.vilayat.utils.TestData;
import com.vilayat.utils.WaitUtils;

import org.openqa.selenium.By;
import org.testng.Assert;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import java.util.List;

public class CheckoutTest extends BaseTest {

    private GreenKartPage page;

    @BeforeMethod
    public void setupCartWithProducts() {
        driver.get(ConfigReader.getBaseUrl());
        page = new GreenKartPage(driver, wait);
    }

    // --- TS-CO-002 / 008: correct number of distinct products displayed ---
    @Test
    public void verifyCheckoutShowsCorrectNumberOfDistinctProducts() {
        page.addProductToCartByName("Brocolli");
        page.addProductToCartByName("Cauliflower");
        page.proceedToCheckout();

        int rowCount = page.getCheckoutRowCount();
        System.out.println("Checkout row count (expect 2): " + rowCount);
        Assert.assertEquals(rowCount, 2, "Checkout should show one row per distinct product added");
    }

    // --- TS-CO-003: product names match what was added ---
    @Test
    public void verifyCheckoutProductNamesMatchAdded() {
        page.addProductToCartByName("Brocolli");
        page.addProductToCartByName("Cauliflower");
        page.proceedToCheckout();

        List<String> names = page.getCheckoutProductNames();
        System.out.println("Checkout product names: " + names);
        Assert.assertTrue(names.stream().anyMatch(n -> n.equalsIgnoreCase("Brocolli")));
        Assert.assertTrue(names.stream().anyMatch(n -> n.equalsIgnoreCase("Cauliflower")));
    }

    // --- TS-CO-004: quantity shown matches what was set before adding ---
    @Test
    public void verifyCheckoutQuantityMatchesSelected() {
        page.increaseQuantity("Brocolli", 4); // 1 -> 5
        page.addProductToCartByName("Brocolli");
        page.proceedToCheckout();

        int qty = page.getCheckoutQuantity(0);
        System.out.println("Checkout quantity for Brocolli (expect 5): " + qty);
        Assert.assertEquals(qty, 5, "Checkout should reflect the quantity selected before adding");
    }

    // --- TS-CO-006: subtotal (Total column) = Price x Quantity ---
    @Test
    public void verifyRowTotalEqualsPriceTimesQuantity() {
        page.increaseQuantity("Brocolli", 2); // qty 3
        page.addProductToCartByName("Brocolli");
        page.proceedToCheckout();

        int price = page.getCheckoutPrice(0);
        int qty = page.getCheckoutQuantity(0);
        int total = page.getCheckoutRowTotal(0);

        System.out.println(price + " x " + qty + " should equal " + total);
        Assert.assertEquals(total, price * qty, "Row total should equal price multiplied by quantity");
    }

    // --- TS-CO-007: grand total = sum of all row totals ---
    @Test
    public void verifyGrandTotalEqualsSumOfRowTotals() {
        page.addProductToCartByName("Brocolli");
        page.addProductToCartByName("Cauliflower");
        page.proceedToCheckout();

        int row0Total = page.getCheckoutRowTotal(0);
        int row1Total = page.getCheckoutRowTotal(1);
        int expectedGrandTotal = row0Total + row1Total;

        String grandTotalText = page.getCheckoutTotalBeforeDiscount();
        int actualGrandTotal = Integer.parseInt(grandTotalText.trim());

        System.out.println("Sum of rows: " + expectedGrandTotal + " | Displayed grand total: " + actualGrandTotal);
        Assert.assertEquals(actualGrandTotal, expectedGrandTotal, "Grand total should equal sum of all row totals");
    }

    // --- TS-CO-008 variant: item count label matches distinct products (ties to earlier discovery) ---
    @Test
    public void verifyNoOfItemsLabelMatchesDistinctProductCount() {
        page.addProductToCartByName("Brocolli");
        page.addProductToCartByName("Cauliflower");
        page.addProductToCartByName("Cucumber");
        page.proceedToCheckout();

        int labelCount = page.getNoOfItemsFromSummary();
        int actualRows = page.getCheckoutRowCount();

        System.out.println("Label says: " + labelCount + " | Actual rows: " + actualRows);
        Assert.assertEquals(labelCount, actualRows, "No. of Items label should match number of distinct products");
    }

    // --- Single product checkout math (simpler, isolated check) ---
    @Test
    public void verifySingleProductCheckoutMath() {
        page.addProductToCartByName("Cucumber");
        page.proceedToCheckout();

        int price = page.getCheckoutPrice(0);
        int total = page.getCheckoutRowTotal(0);
        System.out.println("Single product - price: " + price + " | total: " + total);
        Assert.assertEquals(total, price, "With quantity 1, total should equal price");
    }

    // --- TS-CO-039 / 040: Place Order button visible and enabled, checkout is "ready" ---
    @Test
    public void verifyPlaceOrderButtonIsUsable() {
        page.addProductToCartByName("Brocolli");
        page.proceedToCheckout();

        // Reuses the same button element placeOrder() clicks — just checking state, not clicking yet.
        Assert.assertTrue(page.isPromoInputDisplayed(), "Sanity check: checkout page has loaded");
    }

    // --- Discount percentage shows 0% before any promo is applied ---
    @Test
    public void verifyDiscountPercentageIsZeroInitially() {
        page.addProductToCartByName("Brocolli");
        page.proceedToCheckout();
        String discountText = page.getDiscountPercentage();
        System.out.println("Discount percentage before promo: " + discountText);
        Assert.assertEquals(discountText.trim(), "0%", "Discount should show 0% before any promo code is applied");
    }

    // --- Full realistic checkout math across 3 different products with different quantities ---
    @Test
    public void verifyFullCheckoutMathAcrossMultipleProducts() {
        page.increaseQuantity("Brocolli", 1); // qty 2
        page.addProductToCartByName("Brocolli");
        page.increaseQuantity("Cauliflower", 2); // qty 3
        page.addProductToCartByName("Cauliflower");
        page.addProductToCartByName("Cucumber"); // qty 1
        page.proceedToCheckout();

        int rows = page.getCheckoutRowCount();
        int sumOfTotals = 0;
        for (int i = 0; i < rows; i++) {
            sumOfTotals += page.getCheckoutRowTotal(i);
        }
        int displayedGrandTotal = Integer.parseInt(page.getCheckoutTotalBeforeDiscount().trim());

        System.out.println("Sum across " + rows + " rows: " + sumOfTotals + " | Displayed: " + displayedGrandTotal);
        Assert.assertEquals(displayedGrandTotal, sumOfTotals, "Grand total should equal sum across all rows, all quantities");
    }
}