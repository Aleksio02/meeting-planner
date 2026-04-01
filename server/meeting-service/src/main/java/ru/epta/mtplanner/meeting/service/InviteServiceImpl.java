package ru.epta.mtplanner.meeting.service;

import jakarta.persistence.EntityNotFoundException;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.epta.mtplanner.commons.dao.UserDao;
import ru.epta.mtplanner.commons.dao.dto.UserDto;
import ru.epta.mtplanner.commons.exception.AccessForbiddenException;
import ru.epta.mtplanner.meeting.dao.InviteDao;
import ru.epta.mtplanner.meeting.dao.MeetingDao;
import ru.epta.mtplanner.meeting.dao.dto.InviteDto;

import java.util.UUID;

@Primary
@Service
public class InviteServiceImpl implements InviteService {
    private final MeetingDao meetingDao;
    private final UserDao userDao;
    private final InviteDao inviteDao;


    public InviteServiceImpl(MeetingDao meetingDao, UserDao userDao, InviteDao inviteDao) {
        this.meetingDao = meetingDao;
        this.userDao = userDao;
        this.inviteDao = inviteDao;
    }

    @Override
    @Transactional
    public void deleteInvite(UUID id, UUID currentUserId) {
        InviteDto inviteDto = inviteDao.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Invite not found with id: " + id));

        UUID ownerId = inviteDto.getMeeting().getOwnerId().getId();

        if (!currentUserId.equals(ownerId)) {
            throw new AccessForbiddenException("You are not the owner of the meeting. Only the meeting owner can delete invites.");
        }

        inviteDao.deleteById(id);
    }
}
