package com.vilayat.tests;

import com.vilayat.utils.DBUtils;
import org.testng.Assert;
import org.testng.annotations.Test;

import java.util.List;
import java.util.Map;
import java.util.UUID;

public class DatabaseCRUDTest {

    @Test
    public void createUpdateReadAndDeleteProduct() {
        String productName = "Automation Apple " + UUID.randomUUID();
        boolean inserted = false;

        try {
            int insertedRows = DBUtils.executeParameterizedUpdate(
                    "INSERT INTO products (product_name, price, category) VALUES (?, ?, ?)",
                    productName, 150.00, "Fruit");
            inserted = insertedRows == 1;
            Assert.assertEquals(insertedRows, 1, "Product insertion failed.");

            Assert.assertTrue(
                    DBUtils.upsertProduct(productName, 175.50, null),
                    "Product upsert should report success.");

            List<Map<String, Object>> rows = DBUtils.executeParameterizedQuery(
                    "SELECT price, category FROM products WHERE product_name = ?", productName);
            Assert.assertEquals(rows.size(), 1, "Updated product should be returned exactly once.");
            Assert.assertEquals(((Number) rows.get(0).get("price")).doubleValue(),
                    175.50, 0.001, "Database price mismatch.");
            Assert.assertEquals(rows.get(0).get("category"), "Fruit",
                    "A null category from the UI sync must preserve the existing category.");

        } finally {
            if (inserted) {
                int deletedRows = DBUtils.executeParameterizedUpdate(
                        "DELETE FROM products WHERE product_name = ?", productName);
                Assert.assertEquals(deletedRows, 1, "Test product cleanup failed.");
            }
        }
    }
}
