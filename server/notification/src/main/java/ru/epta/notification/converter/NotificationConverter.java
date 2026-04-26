package ru.epta.notification.converter;

import java.util.UUID;
import org.springframework.stereotype.Component;
import ru.epta.mtplanner.commons.model.notification.InviteNotification;
import ru.epta.mtplanner.commons.model.notification.MeetingNotification;
import ru.epta.mtplanner.commons.model.notification.Notification;
import ru.epta.notification.dao.dto.NotificationDto;

@Component
public class NotificationConverter {

    public NotificationDto toDto(Notification source, UUID receiver) {
        NotificationDto destination = new NotificationDto();
        destination.setActorId(source.getActor().getId());
        destination.setReceiverId(receiver);

        destination.setType(source.getType());
        destination.setSentAt(source.getSentAt());

        switch (source.getType()) {
            case SEND_INVITE, ACCEPT_INVITE, DECLINE_INVITE -> {
                InviteNotification inviteNotification = (InviteNotification) source;
                destination.setMeetingId(inviteNotification.getMeeting().getId());
                destination.setInviteId(inviteNotification.getInviteId());
            }
            case CREATE_MEETING, CANCEL_MEETING -> {
                MeetingNotification meetingNotification = (MeetingNotification) source;
                destination.setMeetingId(meetingNotification.getMeeting().getId());
                // TODO: вернуть, когда добавится миграция
//                destination.setComment(meetingNotification.getComment());
            }
            default -> throw new IllegalStateException("Unknown type of notification");
        }
        return destination;
    }
}
