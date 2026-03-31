package ru.epta.mtplanner.meeting.service;

import jakarta.persistence.EntityNotFoundException;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.epta.mtplanner.commons.dao.UserDao;
import ru.epta.mtplanner.commons.dao.dto.UserDto;
import ru.epta.mtplanner.meeting.converter.InviteConverter;
import ru.epta.mtplanner.meeting.dao.InviteDao;
import ru.epta.mtplanner.meeting.dao.MeetingDao;
import ru.epta.mtplanner.meeting.dao.dto.InviteDto;
import ru.epta.mtplanner.meeting.dao.dto.MeetingDto;
import ru.epta.mtplanner.meeting.model.Invite;
import ru.epta.mtplanner.meeting.model.request.CreateInviteRequest;

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
    public Invite createInvite(CreateInviteRequest request) {
        MeetingDto meetingDto = meetingDao.findById(request.getMeetingId())
                .orElseThrow(() -> new EntityNotFoundException("Meeting not found with id: " + request.getMeetingId()));

        UserDto userDto = userDao.findById(request.getUserId())
                .orElseThrow(() -> new EntityNotFoundException("User not found with id: " + request.getUserId()));

        InviteDto inviteDto = new InviteDto();
        inviteDto.setMeeting(meetingDto);
        inviteDto.setUser(userDto);
        inviteDto.setStatus(request.getStatus());
        inviteDto.setSentAt(request.getSentAt());

        InviteDto savedInvite = inviteDao.save(inviteDto);

        Invite invite = new Invite();
        InviteConverter inviteConverter = new InviteConverter();
        inviteConverter.fromDto(savedInvite, invite);

        return invite;
    }
}
