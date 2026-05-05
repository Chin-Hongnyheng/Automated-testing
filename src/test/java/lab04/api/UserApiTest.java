package lab04.api;

import com.microsoft.playwright.APIResponse;
import org.testng.Assert;
import org.testng.annotations.Test;

public class UserApiTest extends BaseAPITest {

    @Test
    public void getUserTest() {

        APIResponse response = request.get("/users/2");

        System.out.println(response.text());

        Assert.assertEquals(response.status(), 200);
        Assert.assertTrue(response.text().contains("\"id\": 2"));
    }
}