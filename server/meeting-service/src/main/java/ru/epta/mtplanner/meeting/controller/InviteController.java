package ru.epta.mtplanner.meeting.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Nullable;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;
import ru.epta.mtplanner.meeting.config.annotation.CurrentUser;
import ru.epta.mtplanner.meeting.model.Invite;
import ru.epta.mtplanner.meeting.model.Meeting;
import ru.epta.mtplanner.meeting.model.request.CreateInviteRequest;
import ru.epta.mtplanner.meeting.model.request.GetListInviteRequest;
import ru.epta.mtplanner.meeting.model.request.UpdateInviteRequest;
import ru.epta.mtplanner.meeting.model.request.UpdateMeetingRequest;
import ru.epta.mtplanner.meeting.service.InviteService;

import java.util.List;
import java.util.UUID;

@Tag(name = "Приглашения")
@RestController
@RequestMapping("/api/invites")
public class InviteController {
    private final InviteService inviteService;

    public InviteController(InviteService inviteService) {
        this.inviteService = inviteService;
    }

    @GetMapping("/{id}")
    public Invite getInvite(@PathVariable UUID id) {
        return inviteService.getInviteById(id);
    }

    @GetMapping
    public List<Invite> getListInviteRequest(@Nullable @ModelAttribute GetListInviteRequest request) {
        return inviteService.getListInviteRequest(request);
    }

    @PostMapping
    public Invite createInvite(@Valid @RequestBody CreateInviteRequest request, @CurrentUser UUID currentId) {
        return inviteService.createInvite(request, currentId);
    }

    @Operation(summary = "Удалить приглашение",
            description = "Удаляет приглашение по ID")
    @DeleteMapping("/{id}")
    public void deleteInvite(@PathVariable UUID id,
                             @CurrentUser UUID currentUserId) {
        inviteService.deleteInvite(id, currentUserId);
    }

    @PatchMapping("/{id}")
    public Invite updateInvite(@PathVariable UUID id,
                                 @Valid @RequestBody UpdateInviteRequest request,
                                 @CurrentUser UUID currentUserId) {
        return inviteService.updateInvite(id, request, currentUserId);
    }
}
