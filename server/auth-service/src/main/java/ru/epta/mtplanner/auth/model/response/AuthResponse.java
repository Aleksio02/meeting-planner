package ru.epta.mtplanner.auth.model.response;

import lombok.*;
import ru.epta.mtplanner.commons.model.User;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class AuthResponse {
    private String token;
    private User user;
}
