package ru.epta.mtplanner.commons.model.notification;

import lombok.Data;
import ru.epta.mtplanner.commons.model.User;

import java.util.List;
import java.util.UUID;

@Data
public class MeetingNotification extends Notification{

    private MeetingPreview meeting;

    public MeetingNotification() {
        super();
    }

    public MeetingNotification(User actor, MeetingPreview meeting, NotificationType type) {
        super(actor, type);
        this.meeting = meeting;
    }

    @Override
    public List<UUID> extractReceivers() { return List.of(this.actor.getId()); }
}
