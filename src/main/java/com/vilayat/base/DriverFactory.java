package com.vilayat.base;

import java.time.Duration;

import org.openqa.selenium.PageLoadStrategy;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.firefox.FirefoxDriver;
import io.github.bonigarcia.wdm.WebDriverManager;
import com.vilayat.utils.ConfigReader;

public class DriverFactory {
    
    
    private static ThreadLocal<WebDriver> tlDriver = new ThreadLocal<>();

    public static WebDriver createDriver() {
        String browser = ConfigReader.getBrowser().toLowerCase();
        WebDriver driver = null;

        switch (browser) {
            case "chrome":
                WebDriverManager.chromedriver().setup();
                ChromeOptions options = new ChromeOptions();
                options.setPageLoadStrategy(PageLoadStrategy.EAGER);
                if (System.getenv("CI") != null) {
                	options.addArguments("--headless=new");
                    options.addArguments("--no-sandbox");
                    options.addArguments("--disable-dev-shm-usage");
                    options.addArguments("--disable-gpu");
                    options.addArguments("--window-size=1920,1080");
                    options.addArguments("--remote-allow-origins=*");
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

        driver.manage().timeouts().pageLoadTimeout(Duration.ofSeconds(30));
        tlDriver.set(driver);
        return getDriver();
    }

    
    public static synchronized WebDriver getDriver() {
        return tlDriver.get();
    }
    
   
    public static void quitDriver() {
        if (getDriver() != null) {
            getDriver().quit();
            tlDriver.remove(); 
        }
    }
}