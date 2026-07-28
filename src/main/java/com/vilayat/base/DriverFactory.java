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
	            org.openqa.selenium.chrome.ChromeOptions options = new org.openqa.selenium.chrome.ChromeOptions();
	
	            if (System.getenv("CI") != null) {
	                // GitLab automatically sets a "CI" environment variable during pipeline runs.
	                // We detect it here to switch into headless mode ONLY when running in CI —
	                // your local runs stay exactly as they are, with a visible browser.
	                options.addArguments("--headless=new");
	                options.addArguments("--no-sandbox");
	                options.addArguments("--disable-dev-shm-usage");
	                options.addArguments("--window-size=1920,1080");
	            }
	
	            return new ChromeDriver(options);

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