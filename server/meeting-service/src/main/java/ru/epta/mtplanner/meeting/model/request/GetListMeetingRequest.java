package ru.epta.mtplanner.meeting.model.request;

import java.time.LocalDateTime;
import java.util.UUID;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class GetListMeetingRequest {

    private UUID ownerId;
    private LocalDateTime startDate;
    private LocalDateTime finishDate;
    private int limit = 50;
    private int offset = 0;
    private String sortBy = "startsAt";
    private String sortDirection = "ASC";
}
