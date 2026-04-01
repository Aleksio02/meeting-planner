package ru.epta.mtplanner.meeting.service;

import jakarta.persistence.EntityNotFoundException;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.epta.mtplanner.commons.dao.UserDao;
import ru.epta.mtplanner.commons.dao.dto.UserDto;
import ru.epta.mtplanner.commons.exception.AccessForbiddenException;
import ru.epta.mtplanner.commons.exception.IncorrectRequestDataException;
import ru.epta.mtplanner.meeting.converter.InviteConverter;
import ru.epta.mtplanner.meeting.dao.InviteDao;
import ru.epta.mtplanner.meeting.dao.MeetingDao;
import ru.epta.mtplanner.meeting.dao.dto.InviteDto;
import ru.epta.mtplanner.meeting.dao.dto.MeetingDto;
import ru.epta.mtplanner.meeting.model.Invite;
import ru.epta.mtplanner.meeting.model.request.CreateInviteRequest;

import java.time.LocalDateTime;
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
    public Invite getInviteById(UUID id) {

        InviteDto inviteDto = inviteDao.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Invite not found with id: " + id));

        Invite invite = new Invite();
        InviteConverter inviteConverter = new InviteConverter();
        inviteConverter.fromDto(inviteDto, invite);

        return invite;
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

    @Override
    @Transactional
    public Invite createInvite(CreateInviteRequest request, UUID currentId) {
        MeetingDto meetingDto = meetingDao.findById(request.getMeetingId())
                .orElseThrow(() -> new EntityNotFoundException("Meeting not found with id: " + request.getMeetingId()));

        if (!meetingDto.getOwnerId().getId().equals(currentId)) {
            throw new AccessForbiddenException("You are not the owner of this meeting. Only the meeting owner can create invites.");
        }

        UserDto userDto = userDao.findById(request.getUserId())
                .orElseThrow(() -> new EntityNotFoundException("User not found with id: " + request.getUserId()));

        if (userDto.getId().equals(currentId)) {
            throw new IncorrectRequestDataException("You are owner of this meeting.");
        }

        InviteDto inviteDto = new InviteDto();
        inviteDto.setMeeting(meetingDto);
        inviteDto.setUser(userDto);
        inviteDto.setStatus(request.getStatus());
        inviteDto.setSentAt(LocalDateTime.now());

        InviteDto savedInvite = inviteDao.save(inviteDto);

        Invite invite = new Invite();
        InviteConverter inviteConverter = new InviteConverter();
        inviteConverter.fromDto(savedInvite, invite);

        return invite;
    }
}
