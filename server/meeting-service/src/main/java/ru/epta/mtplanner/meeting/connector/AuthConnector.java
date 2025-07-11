package ru.epta.mtplanner.meeting.connector;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import ru.epta.mtplanner.commons.model.User;

@FeignClient(value = "auth-service", url = "http://localhost:8081")
public interface AuthConnector {
    // TODO: aleksioi: добавить аннотацию на тип данных запроса и согласовать контракт
    // (в каком виде отправлять токен, в JSON или строкой и т.д.)
    @PostMapping("/api/auth/validate")
    User validate(String token);
}
