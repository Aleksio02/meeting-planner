package ru.epta.mtplanner.meeting.model.request;

import lombok.Getter;
import lombok.Setter;

import java.util.List;
import java.util.UUID;

@Getter
@Setter
public class CreateListInviteRequest {
    private UUID meetingId;
    private List<UUID> userIds;
}
