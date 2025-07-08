package ru.epta.mtplanner.meeting.controller;

import java.util.List;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.epta.mtplanner.meeting.model.Meeting;
import ru.epta.mtplanner.meeting.model.request.GetListMeetingRequest;
import ru.epta.mtplanner.meeting.service.MeetingService;

@RestController
@RequestMapping("/api/meetings")
public class MeetingController {
    private final MeetingService meetingService;

    public MeetingController(MeetingService meetingService) {this.meetingService = meetingService;}

    @GetMapping
    public List<Meeting> getListMeetingRequest(@ModelAttribute GetListMeetingRequest request) {
        return meetingService.getListMeeting(request);
    }
}
