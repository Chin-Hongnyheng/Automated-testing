package lab04.api;

import com.microsoft.playwright.APIResponse;
import org.testng.Assert;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

public class UserGetTest extends BaseAPITest {

    @DataProvider(name = "userIds")
    public Object[][] userIds() {

        return new Object[][] {
                { 1 },
                { 2 },
                { 3 }
        };
    }

    @Test(dataProvider = "userIds")
    public void getUserById(int id) {

        APIResponse response = request.get("/users/" + id);

        System.out.println(response.text());

        Assert.assertEquals(response.status(), 200);
        Assert.assertTrue(response.text().contains("\"id\": " + id));
    }
}