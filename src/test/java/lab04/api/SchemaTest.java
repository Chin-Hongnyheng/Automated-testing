package lab04.api;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.microsoft.playwright.APIResponse;
import org.testng.Assert;
import org.testng.annotations.Test;

public class SchemaTest extends BaseAPITest {

    @Test
    public void schemaTest() throws Exception {

        APIResponse response = request.get("/posts/1");

        ObjectMapper mapper = new ObjectMapper();

        JsonNode json = mapper.readTree(response.text());

        Assert.assertTrue(json.has("userId"));
        Assert.assertTrue(json.has("id"));
        Assert.assertTrue(json.has("title"));
        Assert.assertTrue(json.has("body"));
    }
}