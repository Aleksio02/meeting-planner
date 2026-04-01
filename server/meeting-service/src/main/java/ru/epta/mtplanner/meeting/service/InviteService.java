package ru.epta.mtplanner.meeting.service;

import org.springframework.stereotype.Service;
import ru.epta.mtplanner.meeting.model.Invite;
import ru.epta.mtplanner.meeting.model.request.CreateInviteRequest;

import java.util.UUID;

@Service
public interface InviteService {
    Invite createInvite(CreateInviteRequest request, UUID currentId);
}
