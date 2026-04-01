package ru.epta.mtplanner.meeting.converter;

import ru.epta.mtplanner.commons.converter.UserConverter;
import ru.epta.mtplanner.commons.model.User;
import ru.epta.mtplanner.meeting.dao.dto.InviteDto;
import ru.epta.mtplanner.meeting.model.Invite;
import ru.epta.mtplanner.meeting.model.Meeting;

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
}
