package ru.epta.mtplanner.auth.service;

import org.springframework.stereotype.Service;
import ru.epta.commons.model.User;
import ru.epta.mtplanner.auth.model.request.Authorization;

@Service
public interface AuthService {
    User login(Authorization request);
}
