package ru.epta.mtplanner.auth.service;

import org.springframework.context.annotation.Primary;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import ru.epta.mtplanner.auth.model.request.GetListRequest;
import ru.epta.mtplanner.commons.dao.UserDao;
import ru.epta.mtplanner.commons.dao.dto.UserDto;

import java.util.List;

@Primary
@Service
public class UserServiceImpl implements UserService {

    private final UserDao userDao;

    public UserServiceImpl(UserDao userDao) {
        this.userDao = userDao;
    }

    @Override
    public List<UserDto> searchUsers(GetListRequest request) {
        String searchString = request.getSearchString();

        int page = request.getPage() != null ? request.getPage() : 0;
        int pageSize = request.getPageSize() != null ? request.getPageSize() : 20;

        Pageable pageable = PageRequest.of(page, pageSize);

        if (searchString == null || searchString.trim().isEmpty()) {
            return userDao.findAllUsers(pageable);
        }

        return userDao.searchUsers(searchString, pageable);
    }
}
