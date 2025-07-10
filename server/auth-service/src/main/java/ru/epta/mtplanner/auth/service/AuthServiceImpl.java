package ru.epta.mtplanner.auth.service;

import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Service;
import ru.epta.commons.converter.UserConverter;
import ru.epta.commons.dao.UserDao;
import ru.epta.commons.dao.dto.UserDto;
import ru.epta.commons.exception.UnauthorizedException;
import ru.epta.commons.model.User;
import ru.epta.mtplanner.auth.hash.PasswordEncoder;
import ru.epta.mtplanner.auth.model.request.Authorization;

@Primary
@Service
public class AuthServiceImpl implements AuthService {

    private final UserDao userDao;
    private final UserConverter userConverter;

    public AuthServiceImpl(UserDao userDao, UserConverter userConverter) {
        this.userDao = userDao;
        this.userConverter = userConverter;
    }

    @Override
    public User login(Authorization request) {
        String login = request.getLogin();
        String password = request.getPassword();

        UserDto userDto = userDao.findByUsernameOrEmail(login)
                .orElseThrow(() -> new UnauthorizedException("User not find"));

        if (!PasswordEncoder.matches(password, userDto.getPassword())) {
            throw new UnauthorizedException("Invalid password");
        }

        User user = new User();
        userConverter.fromDto(userDto, user);

        return user;
    }
}
