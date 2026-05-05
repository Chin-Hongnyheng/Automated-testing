package com.lab03.api;

import org.testng.annotations.Test;
import static io.restassured.RestAssured.*;
import static io.restassured.module.jsv.JsonSchemaValidator.*;

public class SchemaTest extends BaseAPITest {
    @Test
    public void schemaTest() {
        given()
                .when()
                .get("/posts/1")
                .then()
                .statusCode(200)
                .body(matchesJsonSchemaInClasspath("post-schema.json"));
    }
}