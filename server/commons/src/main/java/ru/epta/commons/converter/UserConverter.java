package ru.epta.commons.converter;

import ru.epta.commons.dao.dto.UserDto;
import ru.epta.commons.model.User;

public class UserConverter {
    public void fromDto(UserDto source, User destination){
        destination.setId(source.getId());
        destination.setUsername(source.getUsername());
        destination.setEmail(source.getEmail());
    }
}
