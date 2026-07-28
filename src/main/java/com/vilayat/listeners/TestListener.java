package com.vilayat.listeners;

import com.aventstack.extentreports.ExtentReports;
import com.aventstack.extentreports.ExtentTest;
import com.aventstack.extentreports.Status;
import com.vilayat.base.BaseTest;
import com.vilayat.base.DriverFactory;
import com.vilayat.reports.ExtentManager;
import com.vilayat.utils.ScreenshotUtils;
import org.openqa.selenium.WebDriver;
import org.testng.ITestContext;
import org.testng.ITestListener;
import org.testng.ITestResult;

public class TestListener implements ITestListener {

    private static ExtentReports extent = ExtentManager.getInstance();
    private static ThreadLocal<ExtentTest> test = new ThreadLocal<>();

    @Override
    public void onTestStart(ITestResult result) {
        ExtentTest extentTest = extent.createTest(result.getMethod().getMethodName());
        test.set(extentTest);
    }

    @Override
    public void onTestSuccess(ITestResult result) {
        test.get().log(Status.PASS, "Test passed successfully.");
    }

    @Override
    public void onTestFailure(ITestResult result) {
        test.get().log(Status.FAIL, "Test failed: " + result.getThrowable());

        // Grab the driver from the test instance so we can screenshot at the moment of failure.
        Object currentInstance = result.getInstance();
        if (currentInstance instanceof BaseTest) {
            WebDriver driver = DriverFactory.getDriver();
            String screenshotPath = ScreenshotUtils.captureScreenshot(driver, result.getMethod().getMethodName());
            if (screenshotPath != null) {
                test.get().addScreenCaptureFromPath(screenshotPath);
            }
        }
    }

    @Override
    public void onFinish(ITestContext context) {
        extent.flush();
    }
}