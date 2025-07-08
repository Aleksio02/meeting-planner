package ru.epta.mtplanner.meeting.service;

import java.util.List;
import org.springframework.stereotype.Service;
import ru.epta.mtplanner.meeting.model.Meeting;
import ru.epta.mtplanner.meeting.model.request.GetListMeetingRequest;

@Service
public interface MeetingService {

    List<Meeting> getListMeeting(GetListMeetingRequest request);
}
