package ru.epta.mtplanner.meeting.service;

import org.springframework.stereotype.Service;
import ru.epta.mtplanner.meeting.model.Invite;
import ru.epta.mtplanner.meeting.model.request.CreateInviteRequest;
import ru.epta.mtplanner.meeting.model.request.GetListInviteRequest;

import java.util.UUID;

@Service
public interface InviteService {
    Invite getInviteById(UUID id);
    Invite getListInvite(GetListInviteRequest request);
    Invite createInvite(CreateInviteRequest request, UUID currentId);
}
