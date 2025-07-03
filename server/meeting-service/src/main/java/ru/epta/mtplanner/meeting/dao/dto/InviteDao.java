package ru.epta.mtplanner.meeting.dao.dto;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface InviteDao extends JpaRepository<InviteDto, UUID> {
}
