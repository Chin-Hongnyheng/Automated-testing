package lab01;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.net.http.HttpRequest.Builder;

import org.junit.Before;
import org.junit.Test;

public class AuthenticationTest {
        String url = "http://localhost:3000/auth/login";
        String email = "john@mail.com";
        String pwd = "changeme";
        HttpClient http = HttpClient.newHttpClient();

        Builder builder;

        @Before
        public void initClient() {
                http = HttpClient.newHttpClient();
                builder = HttpRequest.newBuilder()
                                .uri(URI.create(url))
                                .header("Content-Type", "application/json");
        }

        @Test
        public void testLogin() throws IOException, InterruptedException {
                HttpRequest request = builder
                                .POST(HttpRequest.BodyPublishers.ofString(
                                                "{\"email\":\"" + email + "\",\"password\":\"" + pwd + "\"}"))
                                .build();

                HttpResponse<String> response = http.send(request, HttpResponse.BodyHandlers.ofString());

                assertEquals(200, response.statusCode());
        }

        @Test
        public void testLoginFail() throws IOException, InterruptedException {
                String pwd = "123";
                HttpRequest request = builder
                                .POST(HttpRequest.BodyPublishers.ofString(
                                                "{\"email\":\"" + email + "\",\"password\":\"" + pwd + "\"}"))
                                .build();

                HttpResponse<String> response = http.send(request, HttpResponse.BodyHandlers.ofString());
                assertEquals(401, response.statusCode());
        }

        @Test
        public void testLoginEmpty() throws IOException, InterruptedException {
                String email = "";
                String pwd = "";

                HttpRequest request = builder
                                .POST(HttpRequest.BodyPublishers.ofString(
                                                "{\"email\":\"" + email + "\",\"password\":\"" + pwd + "\"}"))
                                .build();
                HttpResponse<String> response = http.send(request, HttpResponse.BodyHandlers.ofString());
                assertEquals(400, response.statusCode());
        }
}
