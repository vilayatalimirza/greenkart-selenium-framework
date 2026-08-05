package com.vilayat.api;

import io.restassured.RestAssured;
import io.restassured.builder.RequestSpecBuilder;
import io.restassured.specification.RequestSpecification;

import org.testng.annotations.BeforeClass;

public class ApiBaseTest {

    @BeforeClass
    public void setupBaseUri() {
        RestAssured.baseURI = "https://reqres.in/api";
        
    String apiKey = System.getenv("REQRES_API_KEY");
    if (apiKey == null) {
        throw new RuntimeException("REQRES_API_KEY environment variable is not set. "
             + "Get a free key at app.reqres.in and set it before running API tests.");
        }
    RequestSpecification requestSpec = new RequestSpecBuilder()
          .addHeader("x-api-key", apiKey)
          .build();

        RestAssured.requestSpecification = requestSpec;
    }
}