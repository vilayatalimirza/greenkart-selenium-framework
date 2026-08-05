package com.vilayat.tests;
import com.vilayat.api.models.UserResponse;
import com.vilayat.api.models.UserRequest;
import io.restassured.response.Response;
import static io.restassured.module.jsv.JsonSchemaValidator.matchesJsonSchemaInClasspath;
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
    @Test
    public void verifyCreateUserReturnsCreatedStatus() {
        String requestBody = "{ \"name\": \"Vilayat\", \"job\": \"QA Automation Engineer\" }";

        Response response = given()
            .header("Content-Type", "application/json")
            .body(requestBody)
        .when()
            .post("/users");

        System.out.println("POST status code: " + response.getStatusCode());
        System.out.println("POST response body: " + response.getBody().asString());

        Assert.assertEquals(response.getStatusCode(), 201, "Creating a user should return 201 Created");
        Assert.assertEquals(response.jsonPath().getString("name"), "Vilayat", "Response should echo back the submitted name");
        Assert.assertEquals(response.jsonPath().getString("job"), "QA Automation Engineer", "Response should echo back the submitted job");
        Assert.assertNotNull(response.jsonPath().getString("id"), "Response should include a generated ID for the new user");
    }
    @Test
    public void verifyUserResponseMatchesSchema() {
        given()
        .when()
            .get("/users/2")
        .then()
            .statusCode(200)
            .body(matchesJsonSchemaInClasspath("schemas/user-schema.json"));
    }
    
    @Test
    public void verifyCreateUserUsingPojo() {
        UserRequest newUser = new UserRequest("Vilayat", "QA Automation Engineer");

        UserResponse createdUser = given()
            .header("Content-Type", "application/json")
            .body(newUser)
        .when()
            .post("/users")
        .then()
            .statusCode(201)
            .extract().as(UserResponse.class);

        System.out.println("Created user ID: " + createdUser.getId());
        Assert.assertEquals(createdUser.getName(), "Vilayat");
        Assert.assertEquals(createdUser.getJob(), "QA Automation Engineer");
        Assert.assertNotNull(createdUser.getId());
    }
}