package ru.epta.mtplanner.auth.model.request;

import lombok.Getter;
import lombok.Setter;
import ru.epta.commons.model.User;

@Getter
@Setter
public class AuthResponse {
    private String token;
    private User user;
}
