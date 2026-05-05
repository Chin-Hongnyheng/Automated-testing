package com.lab03.api;

import org.testng.annotations.Test;
import static io.restassured.RestAssured.*;
import static org.hamcrest.Matchers.*;

public class UserApiTest extends BaseAPITest {
    @Test
    public void getUserTest() {
        given()
                .when()
                .get("/users/2")
                .then()
                .statusCode(200)
                .body("id", equalTo(2))
                .body("name", notNullValue());
    }
}