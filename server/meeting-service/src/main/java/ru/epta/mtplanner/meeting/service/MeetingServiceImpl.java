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
import ru.epta.mtplanner.meeting.model.request.CancelMeetingRequest;
import ru.epta.mtplanner.meeting.model.request.CreateMeetingRequest;
import ru.epta.mtplanner.meeting.model.request.GetListMeetingRequest;
import ru.epta.mtplanner.meeting.model.request.UpdateMeetingRequest;

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

    public MeetingServiceImpl(MeetingDao meetingDao, InviteDao inviteDao, UserDao userDao, NotificationKafkaProducer notificationKafkaProducer) {
        this.meetingDao = meetingDao;
        this.inviteDao = inviteDao;
        this.userDao = userDao;
        this.notificationKafkaProducer = notificationKafkaProducer;
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
        meetingDto.setStartsAt(request.getStartsAt());
        meetingDto.setDuration(request.getDuration());
        meetingDto.setStatus(request.getStatus());

        UserDto owner = userDao.findById(currentId)
                .orElseThrow(() -> new EntityNotFoundException("User not found: " + currentId));
        meetingDto.setOwnerId(owner);

        MeetingDto savedMeeting = meetingDao.save(meetingDto);

        Meeting meeting = new Meeting();
        MeetingConverter meetingConverter = new MeetingConverter();
        meetingConverter.fromDto(savedMeeting, meeting);

        notificationKafkaProducer.sendNotification(meetingConverter.toNotification(meeting, NotificationType.CANCEL_MEETING));

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

        request.getTitle().ifPresent(title -> meetingDto.setTitle(title));
        request.getDescription().ifPresent(description -> meetingDto.setDescription(description));
        request.getStartsAt().ifPresent(startsAt -> meetingDto.setStartsAt(startsAt));
        request.getDuration().ifPresent(duration -> meetingDto.setDuration(duration));
        request.getStatus().ifPresent(status -> meetingDto.setStatus(status));

        MeetingDto savedMeeting = meetingDao.save(meetingDto);

        Meeting meeting = new Meeting();
        MeetingConverter meetingConverter = new MeetingConverter();
        meetingConverter.fromDto(savedMeeting, meeting);

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
