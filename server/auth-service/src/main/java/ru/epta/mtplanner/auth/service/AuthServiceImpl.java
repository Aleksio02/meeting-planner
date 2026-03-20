package ru.epta.mtplanner.auth.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Primary;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import ru.epta.mtplanner.auth.converter.AuthConverter;
import ru.epta.mtplanner.auth.hash.PasswordEncoder;
import ru.epta.mtplanner.auth.model.request.Authorization;
import ru.epta.mtplanner.auth.model.response.AuthResponse;
import ru.epta.mtplanner.auth.utils.SessionUtils;
import ru.epta.mtplanner.commons.converter.UserConverter;
import ru.epta.mtplanner.commons.dao.ProfileDao;
import ru.epta.mtplanner.commons.dao.UserDao;
import ru.epta.mtplanner.commons.dao.dto.UserDto;
import ru.epta.mtplanner.commons.exception.AlreadyExistsException;
import ru.epta.mtplanner.commons.exception.IncorrectRequestDataException;
import ru.epta.mtplanner.commons.exception.UnauthorizedException;
import ru.epta.mtplanner.commons.model.TokenPayload;
import ru.epta.mtplanner.commons.model.User;

import java.time.Instant;
import java.util.List;

@Primary
@Service
public class AuthServiceImpl implements AuthService {

    @Value("${spring.mail.username}")
    private String SOURCE_EMAIL;
    private final UserDao userDao;
    private final ProfileDao profileDao;
    private final JavaMailSender mailSender;
    private final SessionUtils sessionUtils;

    public AuthServiceImpl(UserDao userDao, ProfileDao profileDao, JavaMailSender mailSender, SessionUtils sessionUtils) {
        this.userDao = userDao;
        this.profileDao = profileDao;
        this.mailSender = mailSender;
        this.sessionUtils = sessionUtils;
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

        String token = sessionUtils.createSession(userDto.getId());
        return new AuthResponse(token, user);
    }

    @Override
    public AuthResponse register(Authorization request) {
        if (!request.validToRegistration()) {
            throw new IncorrectRequestDataException("You should fill all fields!");
        }
        userDao.findByUsernameOrEmail(request.getLogin(), request.getEmail()).ifPresent((userDto) -> {
            throw new AlreadyExistsException("User with this username or email exists");
        });

        request.setPassword(PasswordEncoder.encode(request.getPassword()));
        UserDto newUser = new UserDto();
        new AuthConverter().toDto(request, newUser);
        newUser = userDao.save(newUser);

        profileDao.insertProfile(newUser.getId());

        User user = new User();
        new UserConverter().fromDto(newUser, user);

        String sessionId = sessionUtils.createSession(newUser.getId());
        return new AuthResponse(sessionId, user);
    }

    @Override
    public TokenPayload validateSession(String sessionId) {
        RuntimeException sessionHasBeenFinishedException = new UnauthorizedException("This session has been finished");
        try {
            TokenPayload tokenPayload = sessionUtils.getSession(sessionId);
            if (tokenPayload.getExpires().isBefore(Instant.now())) {
                sessionUtils.deleteSession(sessionId);
                throw sessionHasBeenFinishedException;
            }
            return sessionUtils.extendSession(sessionId);
        } catch (Exception e) {
            throw sessionHasBeenFinishedException;
        }
    }

    @Override
    public List<UserDto> searchUsers(String searchString) {
        if (searchString == null || searchString.trim().isEmpty()) {
            return userDao.findAll();
        }
        return userDao.findBySearchString(searchString);
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
