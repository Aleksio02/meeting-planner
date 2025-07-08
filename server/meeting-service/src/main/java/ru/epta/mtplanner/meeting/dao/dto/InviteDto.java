package ru.epta.mtplanner.meeting.dao.dto;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import ru.epta.mtplanner.commons.dao.dto.AbstractEntityDto;
import ru.epta.mtplanner.commons.dao.dto.UserDto;

import java.time.LocalDateTime;

@Getter
@Setter
@Entity
@Table(schema = "public", name = "invites")
public class InviteDto extends AbstractEntityDto {

    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    @JoinColumn(name = "meeting_id", nullable = false)
    private MeetingDto meeting;

    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private UserDto user;

    @Column(name = "sent_at")
    private LocalDateTime sentAt;

    @Column
    private String status;
}
