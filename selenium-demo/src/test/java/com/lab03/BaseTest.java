package com.lab03;

import io.github.bonigarcia.wdm.WebDriverManager;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.testng.ITestResult;
import org.testng.annotations.*;

import java.io.File;
import org.apache.commons.io.FileUtils;
import java.io.IOException;
import org.openqa.selenium.OutputType;
import org.openqa.selenium.TakesScreenshot;
import org.testng.ITestResult;

public class BaseTest {

    protected WebDriver driver;

    @BeforeMethod
    public void setup() {
        WebDriverManager.chromedriver().setup();
        driver = new ChromeDriver();
        driver.manage().window().maximize();
        driver.get("file:///D:/I3/Automated%20Testing/Lab03/selenium-demo/src/main/resources/static/login.html");
    }

    @AfterMethod
    public void teardown(ITestResult result) {
        driver.quit();
    }

    // Screenshot method
    public void takeScreenshot(String name) {
        File src = ((TakesScreenshot) driver).getScreenshotAs(OutputType.FILE);
        try {
            FileUtils.copyFile(src, new File("screenshots/" + name + ".png"));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
