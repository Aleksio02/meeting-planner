package ru.epta.mtplanner.auth.controller;

import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;
import ru.epta.mtplanner.auth.model.request.Authorization;
import ru.epta.mtplanner.auth.model.response.AuthResponse;
import ru.epta.mtplanner.auth.service.AuthService;
import ru.epta.mtplanner.auth.utils.SessionUtils;
import ru.epta.mtplanner.commons.model.TokenPayload;

@RestController
@RequestMapping("/api/auth")
public class AuthController {
    private final AuthService authService;
    private final SessionUtils sessionUtils;

    public AuthController(AuthService authService, SessionUtils sessionUtils) {
        this.authService = authService;
        this.sessionUtils = sessionUtils;
    }

    @PostMapping("/login")
    public AuthResponse login(@Valid @RequestBody Authorization request, HttpServletResponse response) {
        AuthResponse authResponse = authService.login(request);
        sessionUtils.writeSessionCookie(response, authResponse.getToken());
        return authResponse;
    }

    @PostMapping("/register")
    public AuthResponse register(@Valid @RequestBody Authorization request, HttpServletResponse response) {
        AuthResponse authResponse = authService.register(request);
        sessionUtils.writeSessionCookie(response, authResponse.getToken());
        return authResponse;
    }

    @GetMapping("/validateSession")
    public TokenPayload validateSession(@CookieValue(name = "sessionId", required = false) String sessionId) {
        return authService.validateSession(sessionId);
    }
}
