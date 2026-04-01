package ru.epta.mtplanner.meeting.service;

import java.util.UUID;

public interface InviteService {
    void deleteInvite(UUID id, UUID currentUserId);
}
