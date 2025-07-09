package ru.epta.mtplanner.auth.service;

import org.springframework.context.annotation.Primary;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;
import ru.epta.commons.dao.UserDao;
import ru.epta.commons.dao.dto.UserDto;
import ru.epta.commons.model.User;
import ru.epta.mtplanner.auth.model.request.Authorization;

import java.util.Optional;

@Primary
@Service
public class AuthServiceImpl implements AuthService {

    private final UserDao userDao;

    public AuthServiceImpl(UserDao userDao) {
        this.userDao = userDao;
    }

    @Override
    public User login(Authorization request) {
        String login = request.getLogin();
        String password = request.getPassword();

        if (login == null || password == null || login.isBlank() || password.isBlank()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Login and password must not be empty");
        }

        Optional<UserDto> optionalUser = userDao.findByUsername(login);
        if (optionalUser.isEmpty()) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "User not found");
        }

        UserDto userDto = optionalUser.get();

        if (!password.equals(userDto.getPassword())) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Invalid password");
        }

        User user = new User();
        user.setId(userDto.getId());
        user.setUsername(userDto.getUsername());
        user.setEmail(userDto.getEmail());

        return user;
    }
}
