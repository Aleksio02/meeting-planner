package ru.epta.mtplanner.auth.bdd.given;

import io.cucumber.java.Before;
import io.cucumber.java.en.Given;
import org.springframework.beans.factory.annotation.Autowired;
import ru.epta.mtplanner.auth.hash.PasswordEncoder;
import ru.epta.mtplanner.commons.dao.UserDao;
import ru.epta.mtplanner.commons.dao.dto.UserDto;

public class AuthGiven {

    @Autowired
    private UserDao userDao;

    @Before
    public void cleanDatabase() {
        userDao.deleteAll();
    }

    @Given("a user exists with login {string} and password {string}")
    public void aUserExistsWithLoginAndPassword(String login, String password) {
        UserDto user = new UserDto();
        user.setUsername(login);
        user.setEmail(login + "@example.com");
        user.setPassword(PasswordEncoder.encode(password));
        userDao.save(user);
    }

    @Given("a user exists with login {string} and email {string} and password {string}")
    public void aUserExistsWithLoginEmailAndPassword(String login, String email, String password) {
        UserDto user = new UserDto();
        user.setUsername(login);
        user.setEmail(email);
        user.setPassword(PasswordEncoder.encode(password));
        userDao.save(user);
    }

    @Given("no user exists with login {string}")
    public void noUserExistsWithLogin(String login) {
        // no-op: @Before already clears the database
    }

    @Given("no user exists with login {string} and email {string}")
    public void noUserExistsWithLoginAndEmail(String login, String email) {
        // no-op: @Before already clears the database
    }
}
