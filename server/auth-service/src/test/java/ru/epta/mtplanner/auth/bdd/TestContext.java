package ru.epta.mtplanner.auth.bdd;

import io.cucumber.spring.ScenarioScope;
import lombok.Getter;
import lombok.Setter;
import org.springframework.stereotype.Component;

@Component
@ScenarioScope
@Getter
@Setter
public class TestContext {
    private int statusCode;
    private String responseBody;
}
