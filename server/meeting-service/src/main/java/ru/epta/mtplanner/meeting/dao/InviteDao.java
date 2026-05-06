package ru.epta.mtplanner.meeting.dao;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;
import ru.epta.mtplanner.meeting.dao.dto.InviteDto;

import java.util.UUID;

@Repository
public interface InviteDao extends JpaRepository<InviteDto, UUID>, JpaSpecificationExecutor<InviteDto> {
}
