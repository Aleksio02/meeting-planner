package ru.epta.mtplanner.meeting.dao.dto;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import ru.epta.mtplanner.commons.dao.dto.AbstractEntityDto;
import ru.epta.mtplanner.commons.dao.dto.UserDto;
import ru.epta.mtplanner.meeting.model.enums.MeetingStatus;

import java.time.LocalDateTime;

@Getter
@Setter
@Entity
@Table(schema = "public", name = "meetings")
public class MeetingDto extends AbstractEntityDto {

    @Column(nullable = false)
    private String title;

    @Column
    private String description;

    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    @JoinColumn(name = "owner_id", nullable = false)
    private UserDto ownerId;

    @Column
    private LocalDateTime startsAt;

    @Column
    private int duration;

    @Enumerated(EnumType.STRING)
    @Column
    private MeetingStatus status;
}
