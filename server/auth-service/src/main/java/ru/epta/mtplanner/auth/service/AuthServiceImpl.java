package ru.epta.mtplanner.auth.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Primary;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import ru.epta.mtplanner.auth.hash.PasswordEncoder;
import ru.epta.mtplanner.auth.model.request.Authorization;
import ru.epta.mtplanner.auth.model.response.AuthResponse;
import ru.epta.mtplanner.auth.utils.JwtUtils;
import ru.epta.mtplanner.commons.converter.UserConverter;
import ru.epta.mtplanner.commons.dao.UserDao;
import ru.epta.mtplanner.commons.dao.dto.UserDto;
import ru.epta.mtplanner.commons.exception.UnauthorizedException;
import ru.epta.mtplanner.commons.model.User;

@Primary
@Service
public class AuthServiceImpl implements AuthService {

    @Value("${spring.mail.username}")
    private String SOURCE_EMAIL;
    private final UserDao userDao;
    private final JwtUtils jwtUtils;
    private final JavaMailSender mailSender;

    public AuthServiceImpl(UserDao userDao, JwtUtils jwtUtils, JavaMailSender mailSender) {
        this.userDao = userDao;
        this.jwtUtils = jwtUtils;
        this.mailSender = mailSender;
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

    @Async
    protected void sendCodeToMail(String receiver, String code) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setFrom(SOURCE_EMAIL);
        message.setSubject("Активация аккаунта");
        message.setTo(receiver);
        message.setText("Код для активации учетной записи - %s".formatted(code));
        mailSender.send(message);
    }
}
