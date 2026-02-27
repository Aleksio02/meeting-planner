package ru.epta.mtplanner.auth.converter;

import ru.epta.mtplanner.auth.model.request.Authorization;
import ru.epta.mtplanner.commons.dao.dto.UserDto;

public class AuthConverter {
    public void toDto(Authorization source, UserDto destination) {
        destination.setEmail(source.getEmail());
        destination.setUsername(source.getLogin());
        destination.setPassword(source.getPassword());
    }
}
