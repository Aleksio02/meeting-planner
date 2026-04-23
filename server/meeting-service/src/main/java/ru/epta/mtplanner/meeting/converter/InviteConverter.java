package ru.epta.mtplanner.meeting.converter;

import ru.epta.mtplanner.commons.converter.UserConverter;
import ru.epta.mtplanner.commons.model.User;
import ru.epta.mtplanner.commons.model.notification.InviteNotification;
import ru.epta.mtplanner.commons.model.notification.MeetingPreview;
import ru.epta.mtplanner.commons.model.notification.NotificationType;
import ru.epta.mtplanner.meeting.dao.dto.InviteDto;
import ru.epta.mtplanner.meeting.model.Invite;
import ru.epta.mtplanner.meeting.model.Meeting;
import ru.epta.mtplanner.meeting.model.enums.InviteStatus;

import java.util.UUID;

public class InviteConverter {

    public void fromDto(InviteDto source, Invite destination) {
        if (source == null || destination == null) {
            return;
        }

        destination.setId(source.getId());

        if (source.getMeeting() != null) {
            MeetingConverter meetingConverter = new MeetingConverter();
            Meeting meeting = new Meeting();
            meetingConverter.fromDto(source.getMeeting(), meeting);
            destination.setMeetingId(meeting);
        }

        if (source.getUser() != null) {
            UserConverter userConverter = new UserConverter();
            User user = new User();
            userConverter.fromDto(source.getUser(), user);
            destination.setUserId(user);
        }

        destination.setStatus(source.getStatus().getStatus());
        destination.setSentAt(source.getSentAt());
    }

    public InviteNotification toNotification(Invite source, NotificationType type) {
        User actor;
        UUID receiver;

        if (type == NotificationType.SEND_INVITE) {
            actor = source.getMeetingId().getOwner();
            receiver = source.getUserId().getId();
        } else {
            actor = source.getUserId();
            receiver = source.getMeetingId().getOwner().getId();
        }

        InviteNotification inviteNotification = new InviteNotification(actor, receiver, type);

        inviteNotification.setInviteId(source.getId());

        MeetingPreview meetingPreview = new MeetingPreview();
        meetingPreview.setId(source.getMeetingId().getId());
        meetingPreview.setTitle(source.getMeetingId().getTitle());
        inviteNotification.setMeeting(meetingPreview);

        return inviteNotification;
    }
}
