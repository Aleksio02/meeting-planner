package ru.epta.mtplanner.auth.bdd;

import io.cucumber.junit.Cucumber;
import io.cucumber.junit.CucumberOptions;
import org.junit.runner.RunWith;

@RunWith(Cucumber.class)
@CucumberOptions(
        features = "src/test/resources/bdd/features",
        glue = {
                "ru.epta.mtplanner.auth.bdd",
                "ru.epta.mtplanner.auth.bdd.given",
                "ru.epta.mtplanner.auth.bdd.when",
                "ru.epta.mtplanner.auth.bdd.then"
        },
        plugin = {
                "pretty",
                "html:target/cucumber-reports/cucumber.html",
                "json:target/cucumber-reports/cucumber.json"
        }
)
public class CucumberIntegrationTest {
}
