package ru.epta.mtplanner.meeting.converter;

import ru.epta.mtplanner.commons.converter.UserConverter;
import ru.epta.mtplanner.commons.model.User;
import ru.epta.mtplanner.meeting.dao.dto.MeetingDto;
import ru.epta.mtplanner.meeting.model.Meeting;

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
}
