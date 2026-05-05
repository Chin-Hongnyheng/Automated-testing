package com.lab03.api;

import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;
import static io.restassured.RestAssured.*;
import static org.hamcrest.Matchers.*;

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
        given()
                .when()
                .get("/users/" + id)
                .then()
                .statusCode(200)
                .body("id", equalTo(id))
                .body("name", notNullValue())
                .body("email", notNullValue());
    }
}