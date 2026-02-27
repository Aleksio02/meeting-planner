package ru.epta.mtplanner.meeting.connector;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import ru.epta.mtplanner.commons.model.User;

@FeignClient(value = "auth-service", url = "http://localhost:8081")
public interface AuthConnector {
    // TODO: aleksioi: возвращает TokenPayload (ждать мержа TASK-021)
    @PostMapping("/api/auth/validateSession")
    User validateSession(String token);
}
