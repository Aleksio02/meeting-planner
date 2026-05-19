package ru.epta.mtplanner.meeting.service;

import jakarta.persistence.EntityNotFoundException;
import org.springframework.context.annotation.Primary;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.epta.mtplanner.commons.converter.UserConverter;
import ru.epta.mtplanner.commons.dao.UserDao;
import ru.epta.mtplanner.commons.dao.dto.UserDto;
import ru.epta.mtplanner.commons.exception.AccessForbiddenException;
import ru.epta.mtplanner.commons.exception.IncorrectRequestDataException;
import ru.epta.mtplanner.commons.model.User;
import ru.epta.mtplanner.commons.model.notification.MeetingPreview;
import ru.epta.mtplanner.commons.model.notification.NotificationType;
import ru.epta.mtplanner.commons.model.User;
import ru.epta.mtplanner.commons.model.notification.MeetingNotification;
import ru.epta.mtplanner.commons.model.notification.MeetingPreview;
import ru.epta.mtplanner.commons.model.notification.Notification;
import ru.epta.mtplanner.commons.model.notification.NotificationType;
import ru.epta.mtplanner.meeting.converter.MeetingConverter;
import ru.epta.mtplanner.meeting.dao.InviteDao;
import ru.epta.mtplanner.meeting.dao.MeetingDao;
import ru.epta.mtplanner.meeting.dao.dto.InviteDto;
import ru.epta.mtplanner.meeting.dao.dto.MeetingDto;
import ru.epta.mtplanner.meeting.dao.specification.MeetingSpecification;
import ru.epta.mtplanner.meeting.model.Meeting;
import ru.epta.mtplanner.meeting.model.enums.InviteStatus;
import ru.epta.mtplanner.meeting.model.enums.MeetingStatus;
import ru.epta.mtplanner.meeting.model.request.*;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Primary
@Service
public class MeetingServiceImpl implements MeetingService {
    private final MeetingDao meetingDao;

    private final InviteDao inviteDao;

    private final UserDao userDao;
    private final NotificationKafkaProducer notificationKafkaProducer;

    private final InviteService inviteService;

    public MeetingServiceImpl(MeetingDao meetingDao, InviteDao inviteDao, UserDao userDao, NotificationKafkaProducer notificationKafkaProducer, InviteService inviteService) {
        this.meetingDao = meetingDao;
        this.inviteDao = inviteDao;
        this.userDao = userDao;
        this.notificationKafkaProducer = notificationKafkaProducer;
        this.inviteService = inviteService;
    }

    @Override
    public Meeting getMeetingById(UUID id) {
        MeetingDto meetingDto = meetingDao.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Meeting not found with id: " + id));

        Meeting meeting = new Meeting();
        MeetingConverter meetingConverter = new MeetingConverter();
        meetingConverter.fromDto(meetingDto, meeting);

        return meeting;
    }

    @Override
    public List<Meeting> getListMeeting(GetListMeetingRequest request) {
        if (request == null) {
            request = new GetListMeetingRequest();
        }
        Pageable pageable = PageRequest.of(
                request.getOffset() / request.getLimit(),
                request.getLimit(),
                Sort.by(Sort.Direction.fromString(request.getSortDirection()), request.getSortBy())
        );


        Page<MeetingDto> foundMeetings = meetingDao.findAll(MeetingSpecification.build(request), pageable);
        List<Meeting> meetings = new ArrayList<>(foundMeetings.getSize());
        MeetingConverter meetingConverter = new MeetingConverter();
        for (var meetingDto : foundMeetings) {
            Meeting meeting = new Meeting();
            meetingConverter.fromDto(meetingDto, meeting);
            meetings.add(meeting);
        }

        return meetings;
    }


    @Override
    @Transactional
    public Meeting createMeeting(CreateMeetingRequest request, UUID currentId) {

        MeetingDto meetingDto = new MeetingDto();
        meetingDto.setTitle(request.getTitle());
        meetingDto.setDescription(request.getDescription());
        meetingDto.setStartsAt(LocalDateTime.now());
        meetingDto.setDuration(request.getDuration());
        meetingDto.setStatus(request.getStatus());

        UserDto owner = userDao.findById(currentId)
                .orElseThrow(() -> new EntityNotFoundException("User not found: " + currentId));
        meetingDto.setOwnerId(owner);

        MeetingDto savedMeeting = meetingDao.save(meetingDto);

        Meeting meeting = new Meeting();
        MeetingConverter meetingConverter = new MeetingConverter();
        meetingConverter.fromDto(savedMeeting, meeting);

        notificationKafkaProducer.sendNotification(meetingConverter.toNotification(meeting, NotificationType.CREATE_MEETING));

        List<UUID> userIds = request.getInvitedUserIds();
        if (!userIds.isEmpty()) {
            CreateListInviteRequest inviteRequest = new CreateListInviteRequest();
            inviteRequest.setMeetingId(meetingDto.getId());
            inviteRequest.setUserIds(userIds);
            inviteService.createListInvite(inviteRequest, currentId);
        }

        return meeting;
    }

    @Override
    @Transactional
    public void deleteMeeting(UUID id, UUID currentUserId) {
        MeetingDto meetingDto = meetingDao.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Meeting not found with id: " + id));

        UUID ownerID = meetingDto.getOwnerId().getId();

        if (!currentUserId.equals(ownerID)) {
            throw new AccessForbiddenException("You are not the owner of this meeting. Only the meeting owner can delete meetings.");
        }

        meetingDao.deleteById(id);
    }

    @Override
    @Transactional
    public Meeting updateMeeting(UUID id, UpdateMeetingRequest request, UUID currentUserId) {
        MeetingDto meetingDto = meetingDao.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Meeting not found with id: " + id));

        UUID ownerId = meetingDto.getOwnerId().getId();

        if (!currentUserId.equals(ownerId)) {
            throw new AccessForbiddenException("You are not the owner of the meeting. Only the meeting owner can update it.");
        }

        final boolean[] isDetailsChanged = {false};
        final boolean[] isStartDateChanged = {false};

        request.getStartsAt().ifPresent(startsAt -> {
            meetingDto.setStartsAt(startsAt);
            isStartDateChanged[0] = true;

        });

        request.getTitle().ifPresent(title -> {
            meetingDto.setTitle(title);
            isDetailsChanged[0] = true;
        });

        request.getDescription().ifPresent(description -> {
            meetingDto.setDescription(description);
            isDetailsChanged[0] = true;
        });

        request.getDuration().ifPresent(duration -> {
            meetingDto.setDuration(duration);
            isDetailsChanged[0] = true;
        });

        request.getStatus().ifPresent(status -> meetingDto.setStatus(status));

        if (!isDetailsChanged[0] && !isStartDateChanged[0]) {
            throw new IncorrectRequestDataException("No changes detected");
        }

        MeetingDto savedMeeting = meetingDao.save(meetingDto);

        Meeting meeting = new Meeting();
        MeetingConverter meetingConverter = new MeetingConverter();
        meetingConverter.fromDto(savedMeeting, meeting);

        List<InviteDto> participants = inviteDao.findAllByMeetingIdAndStatus(meetingDto.getId(), InviteStatus.ACCEPTED);

        List<UUID> receivers = new ArrayList<>(participants.size());
        participants.stream()
                .map(inv -> inv.getUser().getId())
                .forEach(receivers::add);


        if (isDetailsChanged[0]) {
            notificationKafkaProducer.sendNotification(meetingConverter.toNotification(meeting, NotificationType.UPDATE_MEETING_DETAILS, null, receivers));
        }

        if (isStartDateChanged[0]) {
            notificationKafkaProducer.sendNotification(meetingConverter.toNotification(meeting, NotificationType.RESCHEDULE_MEETING, null, receivers));
        }

        return meeting;
    }

    @Override
    @Transactional
    public Meeting cancelMeeting(UUID id, CancelMeetingRequest request, UUID currentUserId) {
        MeetingDto meetingDto = meetingDao.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Meeting not found with id: " + id));

        UUID ownerId = meetingDto.getOwnerId().getId();

        if (!currentUserId.equals(ownerId)) {
            throw new AccessForbiddenException("You are not the owner of the meeting. Only the meeting owner can update it.");
        }

        if (meetingDto.getStatus() == MeetingStatus.CANCELED) {
            throw new IllegalStateException("Meeting is already cancelled");
        }

        meetingDto.setStatus(MeetingStatus.CANCELED);
        meetingDto.setCancellationReason(request.getReason());
        meetingDto.setCancelledAt(LocalDateTime.now());

        MeetingDto cancelledMeeting = meetingDao.save(meetingDto);

        Meeting meeting = new Meeting();
        MeetingConverter meetingConverter = new MeetingConverter();
        meetingConverter.fromDto(cancelledMeeting, meeting);


        List<InviteDto> participants = inviteDao.findAllByMeetingIdAndStatus(meetingDto.getId(), InviteStatus.ACCEPTED);
        List<UUID> participantIds = participants.stream()
                .map(inv -> inv.getUser().getId())
                .toList();

        notificationKafkaProducer.sendNotification(meetingConverter.toNotification(meeting, NotificationType.CANCEL_MEETING, request.getReason(), participantIds));

        return meeting;
    }

}
