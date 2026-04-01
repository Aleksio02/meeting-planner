package ru.epta.mtplanner.meeting.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;
import ru.epta.mtplanner.meeting.config.annotation.CurrentUser;
import ru.epta.mtplanner.meeting.model.Invite;
import ru.epta.mtplanner.meeting.model.request.CreateInviteRequest;
import ru.epta.mtplanner.meeting.service.InviteService;

import java.util.UUID;

@Tag(name = "Приглашения")
@RestController
@RequestMapping("/api/invites")
public class InviteController {
    private final InviteService inviteService;

    public InviteController(InviteService inviteService) {this.inviteService = inviteService;}


    @GetMapping("/{id}")
    public Invite getInvite(@PathVariable UUID id) {
        return inviteService.getInviteById(id);
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
}

