package ru.epta.mtplanner.commons.model.notification;

import java.util.List;
import java.util.UUID;
import lombok.Data;

@Data
public abstract class Notification {
    protected UUID actor;
    protected NotificationType type;

    public Notification(){}

    public Notification(UUID actor, NotificationType type) {
        this.actor = actor;
        this.type = type;
    }

    public abstract List<UUID> extractReceivers();
}
