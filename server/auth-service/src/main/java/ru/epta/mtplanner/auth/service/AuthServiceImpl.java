package ru.epta.mtplanner.auth.service;

import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Service;
import ru.epta.commons.converter.UserConverter;
import ru.epta.commons.dao.UserDao;
import ru.epta.commons.dao.dto.UserDto;
import ru.epta.commons.exception.UnauthorizedException;
import ru.epta.commons.model.User;
import ru.epta.mtplanner.auth.hash.PasswordEncoder;
import ru.epta.mtplanner.auth.utils.JwtUtils;
import ru.epta.mtplanner.auth.model.request.Authorization;
import ru.epta.mtplanner.auth.model.response.AuthResponse;

@Primary
@Service
public class AuthServiceImpl implements AuthService {

    private final UserDao userDao;

    private final JwtUtils jwtUtils;

    public AuthServiceImpl(UserDao userDao, JwtUtils jwtUtils) {
        this.userDao = userDao;
        this.jwtUtils = jwtUtils;
    }

    @Override
    public AuthResponse login(Authorization request) {
        String login = request.getLogin();
        String password = request.getPassword();

        UserDto userDto = userDao.findByUsernameOrEmail(login)
                .orElseThrow(() -> new UnauthorizedException("User not found"));

        if (!PasswordEncoder.matches(password, userDto.getPassword())) {
            throw new UnauthorizedException("Invalid password");
        }

        User user = new User();
        new UserConverter().fromDto(userDto, user);

        String token = jwtUtils.generateToken(user);
        return new AuthResponse(token, user);
    }
}
