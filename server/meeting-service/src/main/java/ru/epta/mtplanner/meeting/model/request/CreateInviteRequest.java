package ru.epta.mtplanner.meeting.model.request;

import lombok.Getter;
import lombok.Setter;
import ru.epta.mtplanner.meeting.model.enums.InviteStatus;

import java.time.LocalDateTime;
import java.util.UUID;

@Getter
@Setter
public class CreateInviteRequest {

    private UUID meetingId;
    private UUID userId;
    private InviteStatus status;
    private LocalDateTime sentAt;

}
