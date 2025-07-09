package ru.epta.mtplanner.auth.controller;

import org.springframework.web.bind.annotation.*;
import ru.epta.commons.model.User;
import ru.epta.mtplanner.auth.model.request.Authorization;
import ru.epta.mtplanner.auth.service.AuthService;

@RestController
@RequestMapping("/auth")
public class AuthController {
    private final AuthService authService;

    public AuthController(AuthService authService) {this.authService = authService;}

    @PostMapping("/login")
    public User login(@RequestBody Authorization request) {
        return authService.login(request);
    }
}
