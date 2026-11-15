package com.vilayat.tests;

import com.vilayat.utils.DBUtils;
import org.testng.Assert;
import org.testng.annotations.Test;

import java.util.List;
import java.util.Map;

public class DatabaseSmokeTest {

    @Test
    public void verifyProductRetrievalFromDatabase() {
        String query = "SELECT price, category FROM products WHERE product_name = ?";
        String targetProduct = "Brocolli";
        List<Map<String, Object>> rows =
                DBUtils.executeParameterizedQuery(query, targetProduct);

        Assert.assertFalse(rows.isEmpty(), "No data found for product: " + targetProduct);
        double actualPrice = ((Number) rows.get(0).get("price")).doubleValue();
        String actualCategory = (String) rows.get(0).get("category");

        Assert.assertEquals(actualPrice, 60.00, 0.001, "Product price mismatch in DB.");
        Assert.assertEquals(actualCategory, "Vegetable", "Product category mismatch in DB.");
    }
}
