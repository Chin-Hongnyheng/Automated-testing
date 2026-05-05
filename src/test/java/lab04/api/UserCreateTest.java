package lab04.api;

import com.microsoft.playwright.APIResponse;
import com.microsoft.playwright.options.RequestOptions;
import org.testng.Assert;
import org.testng.annotations.Test;

public class UserCreateTest extends BaseAPITest {

    @Test
    public void createUser() {

        String body = """
                {
                  "title":"Test Post",
                  "body":"This is a test",
                  "userId":1
                }
                """;

        APIResponse response = request.post(
                "/posts",
                RequestOptions.create()
                        .setData(body));

        System.out.println(response.text());

        // ✔ check status
        Assert.assertEquals(response.status(), 201);

        // ✔ check response contains id (this API always returns id)
        Assert.assertTrue(response.text().contains("id"));
    }
}