package com.vilayat.base;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.firefox.FirefoxDriver;
import io.github.bonigarcia.wdm.WebDriverManager;
import com.vilayat.utils.ConfigReader;

public class DriverFactory {
    
    // 1. Declare the ThreadLocal variable to isolate WebDriver per thread
    private static ThreadLocal<WebDriver> tlDriver = new ThreadLocal<>();

    public static WebDriver createDriver() {
        String browser = ConfigReader.getBrowser().toLowerCase();
        WebDriver driver = null;

        switch (browser) {
            case "chrome":
                WebDriverManager.chromedriver().setup();
                org.openqa.selenium.chrome.ChromeOptions options = new org.openqa.selenium.chrome.ChromeOptions();

                if (System.getenv("CI") != null) {
                    options.addArguments("--headless=new");
                    options.addArguments("--no-sandbox");
                    options.addArguments("--disable-dev-shm-usage");
                    options.addArguments("--window-size=1920,1080");
                }
                
                driver = new ChromeDriver(options);
                break;

            case "firefox":
                WebDriverManager.firefoxdriver().setup();
                driver = new FirefoxDriver();
                break;

            default:
                throw new IllegalArgumentException(
                    "Unsupported browser in config.properties: " + browser
                );
        }

        // 2. Assign the created driver to the current thread
        tlDriver.set(driver);
        return getDriver();
    }

    // 3. Use this method anywhere in your framework to get the thread-safe driver
    public static synchronized WebDriver getDriver() {
        return tlDriver.get();
    }
    
    // 4. Safely quit the driver and clear the thread memory
    public static void quitDriver() {
        if (getDriver() != null) {
            getDriver().quit();
            tlDriver.remove(); // Essential to prevent memory leaks in parallel execution
        }
    }
}