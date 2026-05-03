package ru.epta.mtplanner.meeting.service;

import org.springframework.stereotype.Service;
import ru.epta.mtplanner.meeting.model.Meeting;
import ru.epta.mtplanner.meeting.model.request.*;

import java.util.List;
import java.util.UUID;

@Service
public interface MeetingService {
    List<Meeting> getListMeeting(GetListMeetingRequest request);

    Meeting getMeetingById(UUID id);

    Meeting createMeeting(CreateMeetingRequest request, UUID currentId);

    void deleteMeeting(UUID id, UUID currentUserId);

    Meeting updateMeeting(UUID id, UpdateMeetingRequest request, UUID currentUserId);

    Meeting cancelMeeting(UUID id, CancelMeetingRequest request, UUID currentUserId);

    Meeting addParticipants(UUID id, AddParticipantsRequest request, UUID currentUserId);

}
