package ru.epta.mtplanner.auth.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.PageRequest;
import ru.epta.mtplanner.auth.model.request.GetListRequest;
import ru.epta.mtplanner.commons.dao.UserDao;
import ru.epta.mtplanner.commons.dao.dto.UserDto;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class UserServiceImplTest {

    @Mock
    UserDao userDao;

    @InjectMocks
    UserServiceImpl userService;

    private GetListRequest buildRequest(String searchString, Integer page, Integer pageSize) {
        GetListRequest request = new GetListRequest();
        request.setSearchString(searchString);
        request.setPage(page);
        request.setPageSize(pageSize);
        return request;
    }

    @Test
    void testSearchUsers_Success() {
        GetListRequest request = buildRequest("o", 0, 10);

        UserDto bob = new UserDto();
        bob.setUsername("Bob");

        UserDto maria = new UserDto();
        maria.setUsername("Maria");

        when(userDao.searchUsers("o", PageRequest.of(0, 10)))
                .thenReturn(List.of(bob));

        List<UserDto> result = userService.searchUsers(request);

        assertEquals(1, result.size());
        assertEquals(bob.getUsername(), result.getFirst().getUsername());

        verify(userDao).searchUsers("o", PageRequest.of(0, 10));
        verifyNoMoreInteractions(userDao);
    }

    @Test
    void testSearchUsers_NullSearchString() {
        GetListRequest request = buildRequest(null, null, null);
        when(userDao.findAllUsers(PageRequest.of(0, 20))).thenReturn(List.of());

        userService.searchUsers(request);

        verify(userDao).findAllUsers(PageRequest.of(0, 20));
        verifyNoMoreInteractions(userDao);
    }

    @Test
    void testSearchUsers_EmptySearchString() {
        GetListRequest request = buildRequest("", null, null);
        when(userDao.findAllUsers(PageRequest.of(0, 20))).thenReturn(List.of());

        userService.searchUsers(request);

        verify(userDao).findAllUsers(PageRequest.of(0, 20));
        verifyNoMoreInteractions(userDao);
    }

    @Test
    void testSearchUsers_DefaultPagination() {
        GetListRequest request = buildRequest("bob", null, null);
        when(userDao.searchUsers("bob", PageRequest.of(0, 20))).thenReturn(List.of());

        userService.searchUsers(request);

        verify(userDao).searchUsers("bob", PageRequest.of(0, 20));
        verifyNoMoreInteractions(userDao);
    }

    @Test
    void testSearchUsers_CustomPagination() {
        GetListRequest request = buildRequest("bob", 2, 5);
        when(userDao.searchUsers("bob", PageRequest.of(2, 5))).thenReturn(List.of());

        userService.searchUsers(request);

        verify(userDao).searchUsers("bob", PageRequest.of(2, 5));
        verifyNoMoreInteractions(userDao);
    }
}
