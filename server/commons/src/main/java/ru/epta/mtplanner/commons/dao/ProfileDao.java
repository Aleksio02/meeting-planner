package ru.epta.mtplanner.commons.dao;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.stereotype.Repository;
import ru.epta.mtplanner.commons.dao.dto.ProfileDto;
import org.springframework.data.jpa.repository.Query;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Repository
public interface ProfileDao extends JpaRepository<ProfileDto, UUID>  {
    @Modifying
    @Transactional
    @Query(value = "INSERT INTO profiles (id) VALUES (?1)", nativeQuery = true)
    void insertProfile(UUID id);
}
