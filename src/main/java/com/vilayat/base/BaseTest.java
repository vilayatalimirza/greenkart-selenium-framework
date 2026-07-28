package com.vilayat.base;

import java.time.Duration;
import org.openqa.selenium.WebDriver;	
import org.openqa.selenium.support.ui.WebDriverWait;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;

import com.vilayat.utils.ConfigReader;


public class BaseTest {

    protected WebDriver driver;
    protected WebDriverWait wait;
    public WebDriver getDriver() {
        return driver;
    }

    @BeforeMethod
    public void setUp() {
    	driver = DriverFactory.createDriver();
    	driver.manage().window().maximize();
    	driver.manage().timeouts().implicitlyWait(Duration.ofSeconds(ConfigReader.getImplicitWait()));
    	wait = new WebDriverWait(driver, Duration.ofSeconds(ConfigReader.getExplicitWait()));
    }

   @AfterMethod
    public void tearDown() {
        if (driver != null) {
            driver.quit();
        }
    }
}