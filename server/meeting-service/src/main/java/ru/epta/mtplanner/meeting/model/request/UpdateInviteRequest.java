package ru.epta.mtplanner.meeting.model.request;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import ru.epta.mtplanner.meeting.model.enums.InviteStatus;

import java.time.LocalDateTime;
import java.util.Optional;

@NoArgsConstructor
@Getter
@Setter
public class UpdateInviteRequest {
    private Optional<InviteStatus> status = Optional.empty();
    private Optional<LocalDateTime> sentAt = Optional.empty();
}
