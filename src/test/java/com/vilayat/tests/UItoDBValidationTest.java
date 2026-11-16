package com.vilayat.tests;

import com.vilayat.base.BaseTest;
import com.vilayat.pages.GreenKartPage;
import com.vilayat.utils.ConfigReader;
import com.vilayat.utils.DBUtils;
import org.testng.Assert;
import org.testng.annotations.Test;

import java.util.List;
import java.util.Map;

public class UItoDBValidationTest extends BaseTest {

    @Test
    public void verifyUIPriceMatchesDatabaseTruth() {
        driver.get(ConfigReader.getBaseUrl());
        GreenKartPage page = new GreenKartPage(driver, wait);
        page.waitForPageLoad();

        String targetProduct = "Brocolli";
        String query = "SELECT price FROM products WHERE product_name = ?";
        List<Map<String, Object>> rows =
                DBUtils.executeParameterizedQuery(query, targetProduct);
        Assert.assertFalse(rows.isEmpty(), "Product not found in test database: " + targetProduct);

        double expectedDbPrice = ((Number) rows.get(0).get("price")).doubleValue();
        double actualUiPrice = page.getProductPrice(targetProduct);

        Assert.assertEquals(actualUiPrice, expectedDbPrice, 0.001,
                "UI price does not match the configured test database baseline.");
    }
}
