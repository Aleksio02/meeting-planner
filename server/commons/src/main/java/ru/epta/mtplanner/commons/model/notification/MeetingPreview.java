package ru.epta.mtplanner.commons.model.notification;

import java.util.UUID;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class MeetingPreview {
    private UUID id;
    private String title;
}
