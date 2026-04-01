package ru.epta.mtplanner.meeting.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Nullable;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;
import ru.epta.mtplanner.meeting.config.annotation.CurrentUser;
import ru.epta.mtplanner.meeting.model.Meeting;
import ru.epta.mtplanner.meeting.model.request.CreateMeetingRequest;
import ru.epta.mtplanner.meeting.model.request.GetListMeetingRequest;
import ru.epta.mtplanner.meeting.service.MeetingService;

import java.util.List;
import java.util.UUID;

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

    @PostMapping
    public Meeting createMeeting(@Valid @RequestBody CreateMeetingRequest request, @CurrentUser UUID currentId) {
        return meetingService.createMeeting(request, currentId);
    }

    @Operation(summary = "Удалить событие",
            description = "Удаляет событие по ID")
    @DeleteMapping("/{id}")
    public void deleteMeeting(@PathVariable UUID id,
                              @CurrentUser UUID currentUserId) {
        meetingService.deleteMeeting(id, currentUserId);
    }

}
