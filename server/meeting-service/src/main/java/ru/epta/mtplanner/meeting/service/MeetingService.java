package ru.epta.mtplanner.meeting.service;

import java.util.List;
import java.util.UUID;

import org.springframework.stereotype.Service;
import ru.epta.mtplanner.meeting.model.Meeting;
import ru.epta.mtplanner.meeting.model.request.GetListMeetingRequest;

@Service
public interface MeetingService {

    List<Meeting> getListMeeting(GetListMeetingRequest request);
    void deleteMeeting(UUID id);
}
