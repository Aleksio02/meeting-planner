package ru.epta.mtplanner.auth.bdd;

import io.cucumber.java.Before;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import ru.epta.mtplanner.auth.utils.SessionUtils;
import ru.epta.mtplanner.commons.dao.ProfileDao;

public class CucumberHooks {

    @Autowired
    private SessionUtils sessionUtils;

    @Autowired
    private ProfileDao profileDao;

    @Before
    public void resetMocks() {
        Mockito.reset(sessionUtils, profileDao);
    }
}
