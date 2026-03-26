package ru.epta.mtplanner.meeting.model.request;

import lombok.Getter;
import lombok.Setter;
import ru.epta.mtplanner.meeting.model.enums.MeetingStatus;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Getter
@Setter
public class CreateMeetingRequest {

    private String title;
    private String description;
    private UUID ownerId;
    private LocalDateTime startsAt;
    private Integer duration;
    private MeetingStatus status;
    private List<UUID> invitedUserIds;

}
