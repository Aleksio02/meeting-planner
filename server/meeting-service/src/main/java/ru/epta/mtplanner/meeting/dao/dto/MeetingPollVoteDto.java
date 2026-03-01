package ru.epta.mtplanner.meeting.dao.dto;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;
import ru.epta.mtplanner.commons.dao.dto.AbstractEntityDto;

import java.util.UUID;

@Getter
@Setter
@Entity
@Table(schema = "public", name = "meeteing_poll_votes")
public class MeetingPollVoteDto extends AbstractEntityDto {

    @Column(nullable = false)
    private UUID meetingPollVariantId;

    @Column(nullable = false)
    private UUID userId;
}
