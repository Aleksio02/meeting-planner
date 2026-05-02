package ru.epta.mtplanner.meeting.converter;

import ru.epta.mtplanner.commons.converter.UserConverter;
import ru.epta.mtplanner.commons.model.User;
import ru.epta.mtplanner.commons.model.notification.InviteNotification;
import ru.epta.mtplanner.commons.model.notification.MeetingNotification;
import ru.epta.mtplanner.commons.model.notification.MeetingPreview;
import ru.epta.mtplanner.commons.model.notification.NotificationType;
import ru.epta.mtplanner.meeting.dao.dto.MeetingDto;
import ru.epta.mtplanner.meeting.model.Invite;
import ru.epta.mtplanner.meeting.model.Meeting;

import java.util.List;
import java.util.UUID;

public class MeetingConverter {

    public void fromDto(MeetingDto source, Meeting destination) {
        destination.setId(source.getId());
        destination.setTitle(source.getTitle());
        destination.setDescription(source.getDescription());

        if (source.getOwnerId() != null) {
            UserConverter userConverter = new UserConverter();
            User owner = new User();
            userConverter.fromDto(source.getOwnerId(), owner);
            destination.setOwner(owner);
        }

        destination.setStartsAt(source.getStartsAt());
        destination.setDuration(source.getDuration());
        destination.setStatus(source.getStatus().getStatus());
    }

    public MeetingNotification toNotification(Meeting source, NotificationType type) {
        MeetingPreview meetingPreview = new MeetingPreview();
        meetingPreview.setId(source.getId());
        meetingPreview.setTitle(source.getTitle());
        MeetingNotification meetingNotification = new MeetingNotification(source.getOwner(), meetingPreview, type);
        meetingNotification.setType(type);

        return meetingNotification;
    }

    public MeetingNotification toNotification(Meeting source, NotificationType type, String comment, List<UUID> receivers) {
        MeetingNotification meetingNotification = toNotification(source, type);
        meetingNotification.setComment(comment);
        meetingNotification.setReceivers(receivers);
        return meetingNotification;
    }
}
