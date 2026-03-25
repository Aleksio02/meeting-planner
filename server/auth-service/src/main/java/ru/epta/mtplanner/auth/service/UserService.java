package ru.epta.mtplanner.auth.service;

import org.springframework.stereotype.Service;
import ru.epta.mtplanner.auth.model.request.GetListRequest;
import ru.epta.mtplanner.commons.dao.dto.UserDto;

import java.util.List;

@Service
public interface UserService {
    List<UserDto> searchUsers(GetListRequest request);
}
