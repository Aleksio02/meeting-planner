package ru.epta.mtplanner.meeting.model.request;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import ru.epta.mtplanner.meeting.model.enums.MeetingStatus;

import java.time.LocalDateTime;
import java.util.Optional;

@NoArgsConstructor
@Getter
@Setter
public class UpdateMeetingRequest {

    private Optional<String> title = Optional.empty();
    private Optional<String> description = Optional.empty();
    private Optional<LocalDateTime> startsAt = Optional.empty();
    private Optional<Integer> duration = Optional.empty();
    private Optional<MeetingStatus> status = Optional.empty();

}