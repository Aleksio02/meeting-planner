package ru.epta.mtplanner.meeting.model.request;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@NoArgsConstructor
@Getter
@Setter
public class CancelMeetingRequest {
    @NotBlank(message = "Причина отмены обязательна")
    private String reason;
}