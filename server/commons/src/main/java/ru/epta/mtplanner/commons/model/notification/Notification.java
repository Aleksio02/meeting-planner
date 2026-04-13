package ru.epta.mtplanner.commons.model.notification;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import lombok.Data;
import ru.epta.mtplanner.commons.model.User;

@Data
public abstract class Notification {
    protected User actor;
    protected NotificationType type;
    protected LocalDateTime sentAt;

    public Notification(){
        this.sentAt = LocalDateTime.now();
    }

    public Notification(User actor, NotificationType type) {
        this();
        this.actor = actor;
        this.type = type;
    }

    public abstract List<UUID> extractReceivers();
}
