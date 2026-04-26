package ru.epta.mtplanner.auth.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mail.javamail.JavaMailSender;
import ru.epta.mtplanner.auth.hash.PasswordEncoder;
import ru.epta.mtplanner.auth.model.request.Authorization;
import ru.epta.mtplanner.auth.model.response.AuthResponse;
import ru.epta.mtplanner.auth.utils.SessionUtils;
import ru.epta.mtplanner.commons.dao.ProfileDao;
import ru.epta.mtplanner.commons.dao.UserDao;
import ru.epta.mtplanner.commons.dao.dto.UserDto;
import ru.epta.mtplanner.commons.exception.AlreadyExistsException;
import ru.epta.mtplanner.commons.exception.IncorrectRequestDataException;
import ru.epta.mtplanner.commons.exception.UnauthorizedException;
import ru.epta.mtplanner.commons.model.TokenPayload;

import java.time.Instant;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class AuthServiceImplTest {

    @Mock
    UserDao userDao;

    @Mock
    ProfileDao profileDao;

    @Mock
    JavaMailSender mailSender;

    @Mock
    SessionUtils sessionUtils;

    @InjectMocks
    AuthServiceImpl authService;

    private Authorization buildAuthorization(String login, String email, String password) {
        Authorization auth = new Authorization();
        auth.setLogin(login);
        auth.setEmail(email);
        auth.setPassword(password);
        return auth;
    }

    private UserDto buildUserDto(String username, String email, String encodedPassword) {
        UserDto userDto = new UserDto();
        userDto.setUsername(username);
        userDto.setEmail(email);
        userDto.setPassword(encodedPassword);
        return userDto;
    }

    @Test
    void testLogin_Success() {
        Authorization request = buildAuthorization("Bob", null, "rawPass");
        UUID userId = UUID.randomUUID();
        UserDto userDto = buildUserDto("Bob", "bob@test.com", PasswordEncoder.encode("rawPass"));
        userDto.setId(userId);

        when(userDao.findByUsernameOrEmail("Bob")).thenReturn(Optional.of(userDto));
        when(sessionUtils.createSession(userId)).thenReturn("token-abc");

        AuthResponse result = authService.login(request);

        assertEquals("token-abc", result.getToken());
        assertNotNull(result.getUser());

        verify(userDao).findByUsernameOrEmail("Bob");
        verify(sessionUtils).createSession(userId);
        verifyNoMoreInteractions(userDao, profileDao, mailSender, sessionUtils);
    }

    @Test
    void testLogin_UserNotFound() {
        Authorization request = buildAuthorization("Bob", null, "rawPass");

        when(userDao.findByUsernameOrEmail("Bob")).thenReturn(Optional.empty());

        assertThrows(UnauthorizedException.class, () -> authService.login(request));

        verify(userDao).findByUsernameOrEmail("Bob");
        verifyNoMoreInteractions(userDao, profileDao, mailSender, sessionUtils);
    }

    @Test
    void testLogin_InvalidPassword() {
        Authorization request = buildAuthorization("Bob", null, "wrongPass");
        UUID userId = UUID.randomUUID();
        UserDto userDto = buildUserDto("Bob", "bob@test.com", PasswordEncoder.encode("correctPass"));
        userDto.setId(userId);

        when(userDao.findByUsernameOrEmail("Bob")).thenReturn(Optional.of(userDto));

        assertThrows(UnauthorizedException.class, () -> authService.login(request));

        verify(userDao).findByUsernameOrEmail("Bob");
        verifyNoMoreInteractions(userDao, profileDao, mailSender, sessionUtils);
    }

    @Test
    void testRegister_Success() {
        Authorization request = buildAuthorization("Bob", "bob@test.com", "password1");
        UUID savedId = UUID.randomUUID();
        UserDto savedUserDto = buildUserDto("Bob", "bob@test.com", PasswordEncoder.encode("password1"));
        savedUserDto.setId(savedId);

        when(userDao.findByUsernameOrEmail("Bob", "bob@test.com")).thenReturn(Optional.empty());
        when(userDao.save(any(UserDto.class))).thenReturn(savedUserDto);
        when(sessionUtils.createSession(savedId)).thenReturn("session-xyz");

        AuthResponse result = authService.register(request);

        assertNotNull(result);
        assertEquals("session-xyz", result.getToken());

        verify(userDao).findByUsernameOrEmail("Bob", "bob@test.com");
        verify(userDao).save(any(UserDto.class));
        verify(profileDao).insertProfile(savedId);
        verify(sessionUtils).createSession(savedId);
        verifyNoMoreInteractions(userDao, profileDao, mailSender, sessionUtils);
    }

    @Test
    void testRegister_InvalidRequest() {
        Authorization request = buildAuthorization("Bob", null, "password1");

        assertThrows(IncorrectRequestDataException.class, () -> authService.register(request));

        verifyNoMoreInteractions(userDao, profileDao, mailSender, sessionUtils);
    }

    @Test
    void testRegister_AlreadyExists() {
        Authorization request = buildAuthorization("Bob", "bob@test.com", "password1");
        UserDto existingUser = buildUserDto("Bob", "bob@test.com", "someHash");

        when(userDao.findByUsernameOrEmail("Bob", "bob@test.com")).thenReturn(Optional.of(existingUser));

        assertThrows(AlreadyExistsException.class, () -> authService.register(request));

        verify(userDao).findByUsernameOrEmail("Bob", "bob@test.com");
        verifyNoMoreInteractions(userDao, profileDao, mailSender, sessionUtils);
    }

    @Test
    void testValidateSession_Success() {
        UUID userId = UUID.randomUUID();
        TokenPayload tokenPayload = new TokenPayload(userId, Instant.now().minusSeconds(100), Instant.now().plusSeconds(3500));
        UserDto userDto = buildUserDto("Bob", "bob@test.com", null);
        userDto.setId(userId);

        when(sessionUtils.getSession("sess-1")).thenReturn(tokenPayload);
        when(userDao.findById(userId)).thenReturn(Optional.of(userDto));
        when(sessionUtils.extendSession("sess-1")).thenReturn(new TokenPayload(userId));

        TokenPayload result = authService.validateSession("sess-1");

        assertNotNull(result.getCurrentUser());
        assertEquals("Bob", result.getCurrentUser().getUsername());

        verify(sessionUtils).getSession("sess-1");
        verify(userDao).findById(userId);
        verify(sessionUtils).extendSession("sess-1");
        verifyNoMoreInteractions(userDao, profileDao, mailSender, sessionUtils);
    }

    @Test
    void testValidateSession_Expired() {
        UUID userId = UUID.randomUUID();
        TokenPayload tokenPayload = new TokenPayload(userId, Instant.now().minusSeconds(7200), Instant.now().minusSeconds(3600));

        when(sessionUtils.getSession("sess-1")).thenReturn(tokenPayload);

        assertThrows(UnauthorizedException.class, () -> authService.validateSession("sess-1"));

        verify(sessionUtils).getSession("sess-1");
        verify(sessionUtils).deleteSession("sess-1");
        verifyNoMoreInteractions(userDao, profileDao, mailSender, sessionUtils);
    }

    @Test
    void testValidateSession_NotFound() {
        when(sessionUtils.getSession("sess-1")).thenThrow(new RuntimeException("Session not found"));

        assertThrows(UnauthorizedException.class, () -> authService.validateSession("sess-1"));

        verify(sessionUtils).getSession("sess-1");
        verifyNoMoreInteractions(userDao, profileDao, mailSender, sessionUtils);
    }
}
