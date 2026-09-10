package com.vilayat.base;

import java.time.Duration;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;

public class BaseTest {
    
    protected WebDriver driver;
    protected WebDriverWait wait;
    protected static final Logger log = LogManager.getLogger(BaseTest.class);

    @BeforeMethod
    public void setUp() {
        log.info("Initializing WebDriver...");
        driver = DriverFactory.createDriver();
        wait = new WebDriverWait(driver, Duration.ofSeconds(60)); 
        log.info("WebDriver initialized successfully.");
    }

    @AfterMethod
    public void tearDown() {
        log.info("Quitting WebDriver...");
        DriverFactory.quitDriver();
        driver = null;
        wait = null;
        log.info("WebDriver quit successfully.");
    }
    
    public WebDriver getDriver() {
        return DriverFactory.getDriver();
    }
}