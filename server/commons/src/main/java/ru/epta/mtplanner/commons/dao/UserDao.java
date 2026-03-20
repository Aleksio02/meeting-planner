package ru.epta.mtplanner.commons.dao;

import org.springframework.data.domain.Pageable;
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

    @Query("""
           SELECT u FROM UserDto u 
           WHERE (:searchString IS NULL OR :searchString = '' 
                  OR LOWER(u.username) LIKE LOWER(CONCAT('%', :searchString, '%')) 
                  OR LOWER(u.email) LIKE LOWER(CONCAT('%', :searchString, '%')))
           ORDER BY u.username
           """)
    List<UserDto> searchUsers(
            @Param("searchString") String searchString,
            Pageable pageable
    );

    @Query("""
           SELECT u FROM UserDto u 
           ORDER BY u.username
           """)
    List<UserDto> findAllUsers(Pageable pageable);
}
