package ru.epta.mtplanner.meeting.dao;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;
import ru.epta.mtplanner.meeting.dao.dto.MeetingDto;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface MeetingDao extends JpaRepository<MeetingDto, UUID>, JpaSpecificationExecutor<MeetingDto> {
    Optional<MeetingDto> findByTitle(String title);
    void deleteById(UUID id);
}
