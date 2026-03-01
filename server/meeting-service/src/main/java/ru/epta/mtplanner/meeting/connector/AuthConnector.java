package ru.epta.mtplanner.meeting.connector;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import ru.epta.mtplanner.commons.model.TokenPayload;
import ru.epta.mtplanner.commons.model.User;

@FeignClient(value = "auth-service", url = "http://localhost:8081")
public interface AuthConnector {
    @GetMapping("/api/auth/validateSession")
    TokenPayload validateSession(@CookieValue(name = "sessionId", required = false) String token);
}
