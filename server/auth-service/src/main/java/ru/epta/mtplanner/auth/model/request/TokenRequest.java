package ru.epta.mtplanner.auth.model.request;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class TokenRequest {
    @NotBlank(message = "Token cannot be blank")
    private String token;
}
