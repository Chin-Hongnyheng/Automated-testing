package lab04;

import com.microsoft.playwright.*;
import org.testng.annotations.*;

public class BaseTest {

    protected Playwright playwright;
    protected Browser browser;
    protected BrowserContext context;
    protected Page page;

    @BeforeMethod
    public void setup() {

        playwright = Playwright.create();

        browser = playwright.chromium().launch(
                new BrowserType.LaunchOptions()
                        .setHeadless(true));

        context = browser.newContext();
        page = context.newPage();

        String path = System.getProperty("user.dir")
                + "/src/main/java/lab04/resources/login.html";

        page.navigate("file://" + path);
    }

    @AfterMethod
    public void teardown() {
        context.close();
        browser.close();
        playwright.close();
    }
}