package ru.epta.mtplanner.auth.controller;

import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.epta.mtplanner.auth.model.request.GetListRequest;
import ru.epta.mtplanner.auth.service.UserService;
import ru.epta.mtplanner.commons.dao.dto.UserDto;

import java.util.List;

@RestController
@RequestMapping("/api/users")
public class UserController {
    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @PostMapping
    public List<UserDto> searchUsers(@Valid @RequestBody GetListRequest request) {
        return userService.searchUsers(request);
    }
}
