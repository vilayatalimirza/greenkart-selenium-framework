package com.vilayat.api;

import io.restassured.RestAssured;
import org.testng.annotations.BeforeClass;

public class ApiBaseTest {

    @BeforeClass
    public void setupBaseUri() {
        RestAssured.baseURI = "https://reqres.in/api";
    }
}