package ru.epta.mtplanner.meeting.service;

import org.springframework.stereotype.Service;
import ru.epta.mtplanner.meeting.model.Invite;
import ru.epta.mtplanner.meeting.model.enums.InviteStatus;
import ru.epta.mtplanner.meeting.model.request.AddParticipantsRequest;
import ru.epta.mtplanner.meeting.model.request.CreateInviteRequest;
import ru.epta.mtplanner.meeting.model.request.GetListInviteRequest;

import java.util.List;
import java.util.UUID;

@Service
public interface InviteService {
    Invite getInviteById(UUID id);

    List<Invite> getListInviteRequest(GetListInviteRequest request);

    Invite createInvite(CreateInviteRequest request, UUID currentId);

    void deleteInvite(UUID id, UUID currentUserId);

    Invite updateInvite(UUID id, InviteStatus status, UUID currentUserId);

    List<Invite> addParticipants(UUID id, AddParticipantsRequest request, UUID currentUserId);
}
