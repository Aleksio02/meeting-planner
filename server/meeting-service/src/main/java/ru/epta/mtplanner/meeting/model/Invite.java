package ru.epta.mtplanner.meeting.model;

import lombok.Getter;
import lombok.Setter;
import ru.epta.commons.model.User;

import java.time.LocalDateTime;
import java.util.UUID;

@Getter
@Setter
public class Invite {
    private UUID id;
    private Meeting meetingId;
    private User userId;
    private String status;
    private LocalDateTime sentAt;
}
