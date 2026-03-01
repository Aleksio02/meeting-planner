package ru.epta.mtplanner.auth.model.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class Authorization {

    @Email
    private String email;

    @NotBlank(message = "Login must not be empty")
    private String login;

    @Size(min=8, max=16, message = "Password should be between 8 and 16 characters")
    @NotBlank(message = "Password must not be empty")
    private String password;

    public boolean validToRegistration() {
        return email != null && login != null && password != null;
    }
}
