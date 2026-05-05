package com.lab03.api;
import io.restassured.RestAssured;
import org.testng.annotations.BeforeClass;

public class BaseAPITest {
    @BeforeClass
    public void setup() {
        // Using JSONPlaceholder - free public API, no authentication needed
        RestAssured.baseURI = "https://jsonplaceholder.typicode.com";
        System.out.println("BASE URI USED: " + RestAssured.baseURI);
    }
}