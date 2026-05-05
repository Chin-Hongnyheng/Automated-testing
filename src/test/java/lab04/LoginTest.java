package lab04;

import lab04.pages.LoginPage;
import lab04.utils.ScreenshotUtil;
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
    public void testLogin(String user, String pass, String expected) {

        LoginPage loginPage = new LoginPage(page);

        loginPage.login(user, pass);

        String actual = loginPage.getMessage();

        ScreenshotUtil.capture(page, user + "_" + actual);

        Assert.assertEquals(actual, expected);
    }
}