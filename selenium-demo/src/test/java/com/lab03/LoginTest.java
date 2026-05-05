package com.lab03;

import com.lab03.pages.LoginPage;
import org.testng.Assert;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

public class LoginTest extends BaseTest {

    @DataProvider(name = "loginData")
    public Object[][] loginData() {

        return new Object[][] {
                { "user1", "pass1", "SUCCESS" },
                { "wrong", "pass1", "FAILED" },
                { "user1", "wrong", "FAILED" },
                { "user2", "wrong", "FAILED" }
        };
    }

    @Test(dataProvider = "loginData")
    public void testLogin(String username, String password, String expected) {

        LoginPage page = new LoginPage(driver);

        page.login(username, password);

        String actual = page.getMessage();

        try {
            Thread.sleep(300);
        } catch (Exception e) {
        }

        takeScreenshot(username + "_" + actual);

        Assert.assertEquals(actual, expected);
    }
}