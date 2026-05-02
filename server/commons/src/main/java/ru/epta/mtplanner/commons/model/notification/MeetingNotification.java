package ru.epta.mtplanner.commons.model.notification;

import lombok.Data;
import ru.epta.mtplanner.commons.model.User;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.UUID;

@Data
public class MeetingNotification extends Notification {

    private MeetingPreview meeting;
    private String comment;
    private List<UUID> receivers = Collections.emptyList();

    public MeetingNotification() {
        super();
    }

    public MeetingNotification(User actor, MeetingPreview meeting, NotificationType type) {
        super(actor, type);
        this.meeting = meeting;
    }
    public MeetingNotification(User actor, MeetingPreview meeting, NotificationType type, String comment, List<UUID> receivers) {
        this(actor, meeting, type);
        this.comment = comment;
        this.receivers = receivers;
    }

    @Override
    public List<UUID> extractReceivers() {
        List<UUID> res = new ArrayList<>(receivers.size() + 1);
        res.add(actor.getId());
        res.addAll(receivers);
        return res;
    }
}
