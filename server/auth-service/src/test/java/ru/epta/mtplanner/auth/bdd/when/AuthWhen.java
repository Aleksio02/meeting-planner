package ru.epta.mtplanner.auth.bdd.when;

import io.cucumber.java.en.When;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import ru.epta.mtplanner.auth.bdd.TestContext;
import ru.epta.mtplanner.auth.model.request.Authorization;
import ru.epta.mtplanner.auth.utils.SessionUtils;

import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

public class AuthWhen {

    @Autowired
    private TestRestTemplate restTemplate;

    @Autowired
    private TestContext testContext;

    @Autowired
    private SessionUtils sessionUtils;

    @LocalServerPort
    private int port;

    @When("the client sends a POST request to {string} with login {string} and password {string}")
    public void loginRequest(String path, String login, String password) {
        when(sessionUtils.createSession(any(UUID.class))).thenReturn("mock-token");
        Authorization body = new Authorization();
        body.setLogin(login);
        body.setPassword(password);
        post(path, body);
    }

    @When("the client sends a POST request to {string} with login {string}, email {string}, and password {string}")
    public void registerRequest(String path, String login, String email, String password) {
        when(sessionUtils.createSession(any(UUID.class))).thenReturn("mock-token");
        Authorization body = new Authorization();
        body.setLogin(login);
        body.setEmail(email);
        body.setPassword(password);
        post(path, body);
    }

    private void post(String path, Authorization body) {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        ResponseEntity<String> raw = restTemplate.postForEntity(
                "http://localhost:" + port + path,
                new HttpEntity<>(body, headers),
                String.class);
        testContext.setStatusCode(raw.getStatusCode().value());
        testContext.setResponseBody(raw.getBody());
    }
}
