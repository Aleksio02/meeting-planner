package ru.epta.mtplanner.meeting.dao;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;
import ru.epta.mtplanner.meeting.dao.dto.InviteDto;
import ru.epta.mtplanner.meeting.model.enums.InviteStatus;

import java.util.List;
import java.util.UUID;

@Repository
public interface InviteDao extends JpaRepository<InviteDto, UUID>, JpaSpecificationExecutor<InviteDto> {
    List<InviteDto> findAllByMeetingIdAndStatus(UUID meetingId, InviteStatus status);
}
