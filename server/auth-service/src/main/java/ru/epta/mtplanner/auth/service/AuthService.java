package ru.epta.mtplanner.auth.service;

import org.springframework.stereotype.Service;
import ru.epta.mtplanner.auth.model.request.Authorization;
import ru.epta.mtplanner.auth.model.response.AuthResponse;
import ru.epta.mtplanner.commons.model.TokenPayload;

@Service
public interface AuthService {
    AuthResponse login(Authorization request);
    AuthResponse register(Authorization request);
    TokenPayload validateSession(String sessionId);
}
