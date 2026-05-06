package ru.epta.mtplanner.commons.model.notification;

import java.util.List;
import java.util.UUID;
import lombok.Data;
import ru.epta.mtplanner.commons.model.User;

@Data
public class InviteNotification extends Notification {
    private UUID receiver;

    private UUID inviteId;

    private MeetingPreview meeting;

    public InviteNotification() {
        super();
    }

    public InviteNotification(User actor, UUID receiver) {
        super(actor, NotificationType.SEND_INVITE);
        this.receiver = receiver;
    }

    @Override
    public List<UUID> extractReceivers() {
        return List.of(this.actor.getId(), this.receiver);
    }
}
