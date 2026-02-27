package ru.epta.mtplanner.meeting.converter;

import ru.epta.mtplanner.meeting.dao.dto.MeetingDto;
import ru.epta.mtplanner.meeting.model.Meeting;

public class MeetingConverter {

    public void fromDto(MeetingDto source, Meeting destination) {
        destination.setId(source.getId());
        destination.setTitle(source.getTitle());
        destination.setDescription(source.getDescription());

        if (source.getOwnerId() != null) {
            // TODO: aleksioi: create UserConverter and convert
        }

        destination.setStartsAt(source.getStartsAt());
        destination.setDuration(source.getDuration());
        destination.setStatus(source.getStatus().getStatus());
    }
}
