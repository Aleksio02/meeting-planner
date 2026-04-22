package ru.epta.mtplanner.meeting.converter;

import ru.epta.mtplanner.commons.converter.UserConverter;
import ru.epta.mtplanner.commons.model.User;
import ru.epta.mtplanner.commons.model.notification.InviteNotification;
import ru.epta.mtplanner.commons.model.notification.InviteResponseNotification;
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
        InviteNotification inviteNotification = new InviteNotification(source.getMeetingId().getOwner(), source.getUserId().getId());
        inviteNotification.setType(type);

        inviteNotification.setInviteId(source.getId());

        MeetingPreview meetingPreview = new MeetingPreview();
        meetingPreview.setId(source.getMeetingId().getId());
        meetingPreview.setTitle(source.getMeetingId().getTitle());
        inviteNotification.setMeeting(meetingPreview);

        return inviteNotification;
    }

    public InviteResponseNotification toResponseNotification(Invite source, UUID receiver, InviteStatus status) {

        UUID inviteId = source.getId();
        MeetingPreview meetingPreview = new MeetingPreview();
        meetingPreview.setId(source.getMeetingId().getId());
        meetingPreview.setTitle(source.getMeetingId().getTitle());

        NotificationType notificationType;
        if (status == InviteStatus.ACCEPTED) {
            notificationType = NotificationType.ACCEPT_INVITE;
        } else {
            notificationType = NotificationType.DECLINE_INVITE;
        }

        return new InviteResponseNotification(
                source.getUserId(),
                receiver,
                inviteId,
                meetingPreview,
                notificationType
        );
    }
}
