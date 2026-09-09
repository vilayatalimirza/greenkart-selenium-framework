package com.vilayat.tests;

import io.restassured.RestAssured;
import io.restassured.response.Response;

import org.openqa.selenium.By;
import org.openqa.selenium.Cookie;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.testng.Assert;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

public class LoginViaAPITest {

    private WebDriver driver;
    private final String baseUrl = "https://the-internet.herokuapp.com";

    @BeforeMethod
    public void loginViaHttpAndInjectCookie() {
        Response loginResponse = RestAssured.given()
                .formParam("username", "tomsmith")
                .formParam("password", "SuperSecretPassword!")
                .redirects().follow(false) 
                .post(baseUrl + "/authenticate");

        // 1. Grab the raw header to prevent RestAssured from auto-decoding %0A into \n
        String rawCookieHeader = loginResponse.getHeader("Set-Cookie");
        
        // 2. Extract the exact raw value securely
        String rawCookieValue = rawCookieHeader.split(";")[0].split("=", 2)[1];

        driver = new ChromeDriver();
        driver.get(baseUrl);

        // 3. Inject the safe, URL-encoded cookie string
        Cookie authCookie = new Cookie.Builder("rack.session", rawCookieValue)
                .domain("the-internet.herokuapp.com")
                .path("/")
                .isHttpOnly(true)
                .build();
                
        driver.manage().addCookie(authCookie);

        driver.get(baseUrl + "/secure"); 
    }

    @Test
    public void verifyLoggedInWithoutSeeingLoginForm() {
        String pageText = driver.findElement(By.tagName("body")).getText();
        Assert.assertTrue(pageText.contains("You logged into a secure area!"),
            "Expected to land on the secure page without ever seeing the login form");
    }
    
    @AfterMethod
    public void tearDown() {
        // Always quit the driver to prevent zombie browser processes
        if (driver != null) {
            driver.quit();
        }
    }
}