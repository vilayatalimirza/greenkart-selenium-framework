package com.vilayat.base;

import java.time.Duration;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;

public class BaseTest {
    
    // Protected instance variable so your test classes (e.g., SearchProductTest) 
    // can access the driver directly without needing to call getDriver() every time.
    protected WebDriver driver;
    protected WebDriverWait wait; // <-- 1. Declare wait here

    @BeforeMethod
    public void setUp() {
        driver = DriverFactory.createDriver();
        
        // 2. Initialize wait so all your test classes inherit it ready-to-use
        wait = new WebDriverWait(driver, Duration.ofSeconds(10)); 
    }

    @AfterMethod
    public void tearDown() {
        DriverFactory.quitDriver();
    }
    
    public WebDriver getDriver() {
        return this.driver;
    }
}