package ru.epta.mtplanner.meeting.service;

import jakarta.persistence.EntityNotFoundException;
import org.springframework.context.annotation.Primary;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.epta.mtplanner.commons.dao.UserDao;
import ru.epta.mtplanner.commons.dao.dto.UserDto;
import ru.epta.mtplanner.commons.exception.AccessForbiddenException;
import ru.epta.mtplanner.commons.exception.IncorrectRequestDataException;
import ru.epta.mtplanner.commons.model.notification.NotificationType;
import ru.epta.mtplanner.meeting.converter.InviteConverter;
import ru.epta.mtplanner.meeting.dao.InviteDao;
import ru.epta.mtplanner.meeting.dao.MeetingDao;
import ru.epta.mtplanner.meeting.dao.dto.InviteDto;
import ru.epta.mtplanner.meeting.dao.dto.MeetingDto;
import ru.epta.mtplanner.meeting.dao.specification.InviteSpecification;
import ru.epta.mtplanner.meeting.model.Invite;
import ru.epta.mtplanner.meeting.model.enums.InviteStatus;
import ru.epta.mtplanner.meeting.model.request.CreateListInviteRequest;
import ru.epta.mtplanner.meeting.model.request.CreateInviteRequest;
import ru.epta.mtplanner.meeting.model.request.GetListInviteRequest;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Primary
@Service
public class InviteServiceImpl implements InviteService {

    private final MeetingDao meetingDao;
    private final UserDao userDao;
    private final InviteDao inviteDao;
    private final NotificationKafkaProducer notificationKafkaProducer;


    public InviteServiceImpl(
            MeetingDao meetingDao,
            UserDao userDao,
            InviteDao inviteDao,
            NotificationKafkaProducer notificationKafkaProducer
    ) {
        this.meetingDao = meetingDao;
        this.userDao = userDao;
        this.inviteDao = inviteDao;
        this.notificationKafkaProducer = notificationKafkaProducer;
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
    public List<Invite> getListInviteRequest(GetListInviteRequest request) {
        if (request == null) {
            request = new GetListInviteRequest();
        }

        Pageable pageable = PageRequest.of(
                request.getPage(),
                request.getPageSize(),
                Sort.by(Sort.Direction.fromString(request.getSortDirection()), request.getSortBy())
        );

        Page<InviteDto> foundInvites = inviteDao.findAll(InviteSpecification.build(request), pageable);
        InviteConverter inviteConverter = new InviteConverter();

        return foundInvites.stream()
                .map(inviteDto -> {
                    Invite invite = new Invite();
                    inviteConverter.fromDto(inviteDto, invite);
                    return invite;
                })
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public Invite createInvite(CreateInviteRequest request, UUID currentId) {
        MeetingDto meetingDto = meetingDao.findById(request.getMeetingId())
                .orElseThrow(() -> new EntityNotFoundException("Meeting not found with id: " + request.getMeetingId()));

        if (!meetingDto.getOwnerId().getId().equals(currentId)) {
            throw new AccessForbiddenException(
                    "You are not the owner of this meeting. Only the meeting owner can create invites.");
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

        notificationKafkaProducer.sendNotification(inviteConverter.toNotification(invite, NotificationType.SEND_INVITE));

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
    public Invite updateInvite(UUID id, InviteStatus status, UUID currentUserId) {
        InviteDto inviteDto = inviteDao.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Invite not found with id: " + id));

        UUID invitedId = inviteDto.getUser().getId();

        if (!currentUserId.equals(invitedId)) {
            throw new AccessForbiddenException("You are not the invited user.");
        }

        inviteDto.setStatus(status);
        InviteDto savedInvite = inviteDao.save(inviteDto);

        Invite invite = new Invite();
        InviteConverter inviteConverter = new InviteConverter();
        inviteConverter.fromDto(savedInvite, invite);

        NotificationType notificationType;
        if (status == InviteStatus.ACCEPTED) {
            notificationType = NotificationType.ACCEPT_INVITE;
        } else {
            notificationType = NotificationType.DECLINE_INVITE;
        }

        notificationKafkaProducer.sendNotification(inviteConverter.toNotification(invite, notificationType));

        return invite;
    }

    @Override
    public List<Invite> createListInvite(CreateListInviteRequest request, UUID currentUserId) {
        List<UUID> participants = request.getUserIds();
        if (participants == null || participants.isEmpty()) {
            throw new IncorrectRequestDataException("Participants list cannot be empty");
        }

        List<Invite> invites = new ArrayList<>();

        for (UUID participant : participants) {
            if (participant == null) {
                throw new IncorrectRequestDataException("User ID cannot be null");
            }

            CreateInviteRequest inviteRequest = new CreateInviteRequest();
            inviteRequest.setMeetingId(request.getMeetingId());
            inviteRequest.setUserId(participant);
            inviteRequest.setStatus(InviteStatus.PENDING);

            invites.add(createInvite(inviteRequest, currentUserId));
        }

        return invites;
    }
}