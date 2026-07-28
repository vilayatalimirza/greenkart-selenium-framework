package com.vilayat.utils;

import java.io.File;
import java.io.IOException;
import org.apache.commons.io.FileUtils;
import org.openqa.selenium.OutputType;
import org.openqa.selenium.TakesScreenshot;
import org.openqa.selenium.WebDriver;

public class ScreenshotUtils {

    public static String captureScreenshot(WebDriver driver, String testName) {
        try {
            TakesScreenshot ts = (TakesScreenshot) driver;
            File source = ts.getScreenshotAs(OutputType.FILE);

            String destPath = System.getProperty("user.dir")
                + "/src/screenshots/" + testName + ".png";
            File destination = new File(destPath);

            FileUtils.copyFile(source, destination);
            return destPath;

        } catch (IOException e) {
            // Screenshot failing should NEVER hide the real test failure that triggered it.
            System.out.println("Failed to capture screenshot: " + e.getMessage());
            return null;
        }
    }
}