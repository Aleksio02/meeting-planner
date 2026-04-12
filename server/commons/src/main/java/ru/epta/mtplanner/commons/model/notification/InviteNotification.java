package ru.epta.mtplanner.commons.model.notification;

import java.util.List;
import java.util.UUID;
import lombok.Data;

@Data
public class InviteNotification extends Notification {
    private UUID receiver;

    public InviteNotification() {
        super();
    }

    public InviteNotification(UUID actor, UUID receiver) {
        super(actor, NotificationType.SEND_INVITE);
        this.receiver = receiver;
    }

    @Override
    public List<UUID> extractReceivers() {
        return List.of(this.actor, this.receiver);
    }
}
