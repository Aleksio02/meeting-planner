package ru.epta.mtplanner.auth.controller;

import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;
import ru.epta.mtplanner.auth.model.request.Authorization;
import ru.epta.mtplanner.auth.model.request.GetListRequest;
import ru.epta.mtplanner.auth.model.response.AuthResponse;
import ru.epta.mtplanner.auth.service.AuthService;
import ru.epta.mtplanner.commons.dao.dto.UserDto;
import ru.epta.mtplanner.commons.model.TokenPayload;

import java.util.List;

@RestController
@RequestMapping("/api/auth")
public class AuthController {
    private final AuthService authService;

    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    @GetMapping("/login")
    public AuthResponse login(@Valid @RequestBody Authorization request) {
        return authService.login(request);
    }

    @PostMapping("/register")
    public AuthResponse register(@Valid @RequestBody Authorization request) {
        return authService.register(request);
    }

    @GetMapping("/validateSession")
    public TokenPayload validateSession(@CookieValue(name = "sessionId", required = false) String sessionId) {
        return authService.validateSession(sessionId);
    }

    @GetMapping("/users")
    public List<UserDto> searchUsers(@Valid @RequestBody GetListRequest request) {
        return authService.searchUsers(request.getSearchString());
    }
}
