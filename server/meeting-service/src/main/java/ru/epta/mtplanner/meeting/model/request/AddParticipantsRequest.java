package ru.epta.mtplanner.meeting.model.request;

import lombok.Getter;
import lombok.Setter;
import ru.epta.mtplanner.commons.model.User;

import java.util.List;

@Getter
@Setter
public class AddParticipantsRequest {
    private List<User> participants;
}
