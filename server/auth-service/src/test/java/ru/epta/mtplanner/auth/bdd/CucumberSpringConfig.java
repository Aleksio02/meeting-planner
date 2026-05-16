package ru.epta.mtplanner.auth.bdd;

import io.cucumber.spring.CucumberContextConfiguration;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.test.context.ActiveProfiles;
import ru.epta.mtplanner.auth.utils.SessionUtils;
import ru.epta.mtplanner.commons.dao.ProfileDao;

@CucumberContextConfiguration
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("test")
public class CucumberSpringConfig {

    @MockitoBean
    SessionUtils sessionUtils;

    @MockitoBean
    JavaMailSender javaMailSender;

    @MockitoBean
    ProfileDao profileDao;
}
