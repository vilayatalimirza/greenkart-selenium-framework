package com.vilayat.base;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.firefox.FirefoxDriver;
import io.github.bonigarcia.wdm.WebDriverManager;
import com.vilayat.utils.ConfigReader;

public class DriverFactory {

    public static WebDriver createDriver() {
        String browser = ConfigReader.getBrowser().toLowerCase();

        switch (browser) {
            case "chrome":
                WebDriverManager.chromedriver().setup();
                return new ChromeDriver();

            case "firefox":
                WebDriverManager.firefoxdriver().setup();
                return new FirefoxDriver();

            default:
                throw new IllegalArgumentException(
                    "Unsupported browser in config.properties: " + browser
                );
        }
    }
}