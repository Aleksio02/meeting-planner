package ru.epta.mtplanner.commons.dao;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import ru.epta.mtplanner.commons.dao.dto.UserDto;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface UserDao extends JpaRepository<UserDto, UUID>, JpaSpecificationExecutor<UserDto> {
    Optional<UserDto> findByUsername(String username);

    Optional<UserDto> findByUsernameOrEmail(String username, String email);

    default Optional<UserDto> findByUsernameOrEmail(String login){
        return findByUsernameOrEmail(login, login);
    }

    @Query("SELECT u FROM UserDto u " +
            "LEFT JOIN ProfileDto p ON u.id = p.id " +
            "WHERE " +
            "LOWER(u.email) LIKE LOWER(CONCAT('%', :searchString, '%')) OR " +
            "LOWER(p.firstName) LIKE LOWER(CONCAT('%', :searchString, '%')) OR " +
            "LOWER(p.lastName) LIKE LOWER(CONCAT('%', :searchString, '%'))")
    List<UserDto> findBySearchString(@Param("searchString") String searchString);
}
