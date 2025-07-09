package ru.epta.commons.dao;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;
import ru.epta.commons.dao.dto.UserDto;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface UserDao extends JpaRepository<UserDto, UUID>, JpaSpecificationExecutor<UserDto> {
    Optional<UserDto> findByUsername(String username);

    Optional<UserDto> findByEmail(String email);
}
