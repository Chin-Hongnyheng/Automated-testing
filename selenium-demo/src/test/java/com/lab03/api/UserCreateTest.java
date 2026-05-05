package com.lab03.api;

import org.testng.annotations.Test;
import static io.restassured.RestAssured.*;
import static org.hamcrest.Matchers.*;

public class UserCreateTest extends BaseAPITest {
    @Test
    public void createUser() {
        String body = """
                {
                    "title": "Test Post",
                    "body": "This is a test",
                    "userId": 1
                }
                """;
        given()
                .header("Content-Type", "application/json")
                .body(body)
                .when()
                .post("/posts")
                .then()
                .statusCode(201)
                .body("title", equalTo("Test Post"))
                .body("id", notNullValue());
    }
}