package ru.epta.mtplanner.meeting.model.request;

import jakarta.validation.constraints.NotEmpty;
import lombok.Getter;
import lombok.Setter;

import java.util.List;
import java.util.UUID;

@Getter
@Setter
public class CreateListInviteRequest {
    private UUID meetingId;

    @NotEmpty(message = "Список не может быть пустым или со значением null")
    private List<UUID> userIds;
}
