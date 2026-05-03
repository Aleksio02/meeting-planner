package ru.epta.mtplanner.meeting.model.request;

import lombok.Getter;
import lombok.Setter;

import java.util.List;
import java.util.UUID;

@Getter
@Setter
public class AddParticipantsRequest {
    private List<UUID> participants;
}
