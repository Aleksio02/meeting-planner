package ru.epta.mtplanner.meeting.controller;

import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
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

    @PostMapping
    public Invite createInvite(@Valid @RequestBody CreateInviteRequest request, @CurrentUser UUID currentId) {
        return inviteService.createInvite(request, currentId);
    }
}
