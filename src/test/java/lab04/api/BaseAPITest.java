package lab04.api;

import com.microsoft.playwright.APIRequest;
import com.microsoft.playwright.APIRequestContext;
import com.microsoft.playwright.Playwright;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;

public class BaseAPITest {

    protected Playwright playwright;
    protected APIRequestContext request;

    @BeforeClass
    public void setup() {

        playwright = Playwright.create();

        request = playwright.request().newContext(
                new APIRequest.NewContextOptions()
                        .setBaseURL("https://jsonplaceholder.typicode.com"));

        System.out.println("BASE URI USED: https://jsonplaceholder.typicode.com");
    }

    @AfterClass
    public void tearDown() {

        request.dispose();

        playwright.close();
    }
}