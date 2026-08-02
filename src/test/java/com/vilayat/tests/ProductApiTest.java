package com.vilayat.tests;

import io.restassured.response.Response;
import com.vilayat.api.*;
import org.testng.Assert;
import org.testng.annotations.Test;

import static io.restassured.RestAssured.given;

public class ProductApiTest extends ApiBaseTest {

    @Test
    public void verifyGetSingleUserReturnsCorrectData() {
        Response response = given()
            .header("Content-Type", "application/json")
        .when()
            .get("/users/2");

        System.out.println("Status code: " + response.getStatusCode());
        System.out.println("Response body: " + response.getBody().asString());

        Assert.assertEquals(response.getStatusCode(), 200, "Expected 200 OK for a valid user ID");
        Assert.assertEquals(response.jsonPath().getInt("data.id"), 2, "Returned user ID should match requested ID");
        Assert.assertTrue(response.jsonPath().getString("data.email").contains("@"),
            "Email field should contain a valid email format");
    }

    @Test
    public void verifyGetNonExistentUserReturns404() {
        Response response = given()
        .when()
            .get("/users/9999");

        System.out.println("Status code for non-existent user: " + response.getStatusCode());
        Assert.assertEquals(response.getStatusCode(), 404, "Requesting a non-existent user should return 404");
    }

    @Test
    public void verifyResponseTimeIsAcceptable() {
        Response response = given()
        .when()
            .get("/users/2");

        long responseTime = response.getTime();
        System.out.println("Response time: " + responseTime + "ms");
        Assert.assertTrue(responseTime < 3000, "Response should return within 3 seconds");
    }
}