package ru.epta.mtplanner.commons.dao;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ru.epta.mtplanner.commons.dao.dto.UserDto;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface UserDao extends JpaRepository<UserDto, UUID> {
    Optional<UserDto> findByUsername(String username);
}
