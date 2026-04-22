package ru.epta.mtplanner.commons.model.notification;

import lombok.Data;
import ru.epta.mtplanner.commons.model.User;

import java.util.List;
import java.util.UUID;

@Data
public class InviteResponseNotification extends Notification {
    private UUID receiver;

    private UUID inviteId;

    private MeetingPreview meeting;

    public InviteResponseNotification() {
        super();
    }

    public InviteResponseNotification(User actor, UUID receiver, UUID inviteId, MeetingPreview meeting, NotificationType type) {
        super(actor, type);
        this.receiver = receiver;
        this.inviteId = inviteId;
        this.meeting = meeting;
    }

    @Override
    public List<UUID> extractReceivers() {
        return List.of(this.receiver);
    }

}
