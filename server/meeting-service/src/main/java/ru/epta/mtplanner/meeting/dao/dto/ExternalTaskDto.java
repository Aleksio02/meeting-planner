package ru.epta.mtplanner.meeting.dao.dto;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import ru.epta.mtplanner.commons.dao.dto.AbstractEntityDto;
import ru.epta.mtplanner.commons.dao.dto.UserDto;
import ru.epta.mtplanner.meeting.model.enums.MeetingStatus;

import java.time.LocalDateTime;
import java.util.UUID;

@Getter
@Setter
@Entity
@Table(schema = "public", name = "external_tasks")
public class ExternalTaskDto extends AbstractEntityDto {

    @Column(nullable = false)
    private Object taskExternalId;

    @Column(nullable = false)
    private String absolutePath;

    @Column(nullable = false)
    private String title;

    @Column(nullable = false)
    private String content;

    @Column(nullable = false)
    private UUID meetingId;
}
