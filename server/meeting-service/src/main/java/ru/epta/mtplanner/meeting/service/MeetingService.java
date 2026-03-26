package ru.epta.mtplanner.meeting.service;

import org.springframework.stereotype.Service;
import ru.epta.mtplanner.meeting.model.Meeting;
import ru.epta.mtplanner.meeting.model.request.CreateMeetingRequest;
import ru.epta.mtplanner.meeting.model.request.GetListMeetingRequest;

import java.util.List;
import java.util.UUID;

@Service
public interface MeetingService {

    List<Meeting> getListMeeting(GetListMeetingRequest request);
    Meeting createMeeting(CreateMeetingRequest request, UUID currentId);
    void deleteMeeting(UUID id);
}
