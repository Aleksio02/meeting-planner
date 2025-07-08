package ru.epta.mtplanner.auth.model.request;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class Authorization {
    private String login;
    private String password;
}
