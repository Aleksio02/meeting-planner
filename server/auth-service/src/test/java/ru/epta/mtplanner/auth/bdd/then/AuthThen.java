package ru.epta.mtplanner.auth.bdd.then;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.cucumber.java.en.And;
import io.cucumber.java.en.Then;
import org.springframework.beans.factory.annotation.Autowired;
import ru.epta.mtplanner.auth.bdd.TestContext;
import ru.epta.mtplanner.auth.model.response.AuthResponse;

import static org.assertj.core.api.Assertions.assertThat;

public class AuthThen {

    @Autowired
    private TestContext testContext;

    private final ObjectMapper mapper = new ObjectMapper();

    @Then("the response status is {int}")
    public void theResponseStatusIs(int expectedStatus) {
        assertThat(testContext.getStatusCode()).isEqualTo(expectedStatus);
    }

    @And("the response body contains a token")
    public void theResponseBodyContainsAToken() throws Exception {
        AuthResponse response = mapper.readValue(testContext.getResponseBody(), AuthResponse.class);
        assertThat(response.getToken()).isNotBlank();
    }

    @And("the response body contains a user with username {string}")
    public void theResponseBodyContainsAUserWithUsername(String username) throws Exception {
        AuthResponse response = mapper.readValue(testContext.getResponseBody(), AuthResponse.class);
        assertThat(response.getUser()).isNotNull();
        assertThat(response.getUser().getUsername()).isEqualTo(username);
    }

    @And("the response body contains error message {string}")
    public void theResponseBodyContainsErrorMessage(String expectedMessage) throws Exception {
        com.fasterxml.jackson.databind.JsonNode node = mapper.readTree(testContext.getResponseBody());
        assertThat(node.get("errorMessage").asText()).isEqualTo(expectedMessage);
    }
}
