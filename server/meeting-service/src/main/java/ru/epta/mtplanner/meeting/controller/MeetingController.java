package ru.epta.mtplanner.meeting.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Nullable;
import java.util.List;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.epta.mtplanner.meeting.config.annotation.RequiresAuth;
import ru.epta.mtplanner.meeting.model.Meeting;
import ru.epta.mtplanner.meeting.model.request.GetListMeetingRequest;
import ru.epta.mtplanner.meeting.service.MeetingService;

@Tag(name = "События")
@RestController
@RequestMapping("/api/meetings")
public class MeetingController {
    private final MeetingService meetingService;

    public MeetingController(MeetingService meetingService) {this.meetingService = meetingService;}

    @Operation(summary = "Получить список событий",
        description = """
        Получить список событий по фильтрам.\n
        Примечания:\n
        Параметры limit, offset, sortBy и sortDirection имеют стандартные значения, поэтому их можно не указывать!
        """)
    @GetMapping
    public List<Meeting> getListMeetingRequest(@Nullable @ModelAttribute GetListMeetingRequest request) {
        return meetingService.getListMeeting(request);
    }
}
