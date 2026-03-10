package ru.epta.mtplanner.meeting.service;

import java.util.ArrayList;
import java.util.List;
import org.springframework.context.annotation.Primary;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import ru.epta.mtplanner.meeting.converter.MeetingConverter;
import ru.epta.mtplanner.meeting.dao.MeetingDao;
import ru.epta.mtplanner.meeting.dao.dto.MeetingDto;
import ru.epta.mtplanner.meeting.dao.specification.MeetingSpecification;
import ru.epta.mtplanner.meeting.model.Meeting;
import ru.epta.mtplanner.meeting.model.request.GetListMeetingRequest;

@Primary
@Service
public class MeetingServiceImpl implements MeetingService {
    private final MeetingDao meetingDao;

    public MeetingServiceImpl(MeetingDao meetingDao) {this.meetingDao = meetingDao;}

    @Override
    public List<Meeting> getListMeeting(GetListMeetingRequest request) {
        if (request == null) {
            request = new GetListMeetingRequest();
        }
        Pageable pageable = PageRequest.of(
            request.getOffset() / request.getLimit(),
            request.getLimit(),
            Sort.by(Sort.Direction.fromString(request.getSortDirection()), request.getSortBy())
        );


        Page<MeetingDto> foundMeetings = meetingDao.findAll(MeetingSpecification.build(request), pageable);
        List<Meeting> meetings = new ArrayList<>(foundMeetings.getSize());
        MeetingConverter meetingConverter = new MeetingConverter();
        for (var meetingDto: foundMeetings) {
            Meeting meeting = new Meeting();
            meetingConverter.fromDto(meetingDto, meeting);
            meetings.add(meeting);
        }

        return meetings;
    }
}
