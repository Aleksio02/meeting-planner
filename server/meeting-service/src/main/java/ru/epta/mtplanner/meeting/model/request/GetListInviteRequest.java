package ru.epta.mtplanner.meeting.model.request;

import lombok.Getter;
import lombok.Setter;
import ru.epta.mtplanner.meeting.model.enums.InviteStatus;

import java.time.LocalDateTime;
import java.util.UUID;

@Getter
@Setter
public class GetListInviteRequest {
    private UUID meetingId;
    private UUID userId;
    private InviteStatus status;
    private LocalDateTime startDate;
    private LocalDateTime finishDate;
    private Integer page = 0;
    private Integer pageSize = 20;
    private String sortBy = "sentAt";
    private String sortDirection = "ASC";
}
