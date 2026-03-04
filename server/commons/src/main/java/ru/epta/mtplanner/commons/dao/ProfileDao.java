package ru.epta.mtplanner.commons.dao;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ru.epta.mtplanner.commons.dao.dto.ProfileDto;

import java.util.UUID;

@Repository
public interface ProfileDao extends JpaRepository<ProfileDto, UUID>  {

}
