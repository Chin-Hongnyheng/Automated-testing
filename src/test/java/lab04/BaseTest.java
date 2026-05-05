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

        page.navigate(
                "file:///home/runner/work/Automated%20Testing/Lab04/playwright-demo/src/main/java/lab04/pages/login.html");
    }

    @AfterMethod
    public void teardown() {
        context.close();
        browser.close();
        playwright.close();
    }

    // SAME ROLE as Selenium screenshot method
    public void takeScreenshot(String name) {
        page.screenshot(new Page.ScreenshotOptions()
                .setPath(java.nio.file.Paths.get("screenshots/" + name + ".png")));
    }
}