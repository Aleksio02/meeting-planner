package ru.epta.mtplanner.meeting.model;

import ru.epta.commons.model.User;

import java.time.LocalDateTime;
import java.util.UUID;

public class Invite {
    private UUID id;
    private Meeting mId;
    private User uId;
    private String status;
    private LocalDateTime sentAt;
}
