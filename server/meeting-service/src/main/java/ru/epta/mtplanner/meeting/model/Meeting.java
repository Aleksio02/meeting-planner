package ru.epta.mtplanner.meeting.model;

import lombok.Getter;
import lombok.Setter;
import ru.epta.commons.model.User;

import java.time.LocalDateTime;
import java.util.UUID;

@Getter
@Setter
public class Meeting {

    private UUID id;
    private String title;
    private String description;
    private User owner;
    private LocalDateTime startsAt;
    private Integer duration;
    private String status;
}
