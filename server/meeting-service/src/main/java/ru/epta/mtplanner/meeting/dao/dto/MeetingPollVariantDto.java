package ru.epta.mtplanner.meeting.dao.dto;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;
import ru.epta.mtplanner.commons.dao.dto.AbstractEntityDto;

import java.time.LocalDateTime;
import java.util.UUID;

@Getter
@Setter
@Entity
@Table(schema = "public", name = "meeting_poll_variants")
public class MeetingPollVariantDto extends AbstractEntityDto {

    @Column(nullable = false)
    private UUID meetingPollId;

    @Column(nullable = false)
    private LocalDateTime vote;
}