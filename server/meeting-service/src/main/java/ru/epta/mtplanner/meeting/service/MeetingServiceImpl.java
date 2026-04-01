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
import ru.epta.mtplanner.meeting.converter.MeetingConverter;
import ru.epta.mtplanner.meeting.dao.MeetingDao;
import ru.epta.mtplanner.meeting.dao.dto.MeetingDto;
import ru.epta.mtplanner.meeting.dao.specification.MeetingSpecification;
import ru.epta.mtplanner.meeting.model.Meeting;
import ru.epta.mtplanner.meeting.model.request.CreateMeetingRequest;
import ru.epta.mtplanner.meeting.model.request.GetListMeetingRequest;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Primary
@Service
public class MeetingServiceImpl implements MeetingService {
    private final MeetingDao meetingDao;
    private final UserDao userDao;


    public MeetingServiceImpl(MeetingDao meetingDao, UserDao userDao) {
        this.meetingDao = meetingDao;
        this.userDao = userDao;
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
        return meeting;
    }

    @Override
    @Transactional
    public void deleteMeeting(UUID id, UUID currentUserId) {
        if (!meetingDao.existsById(id)) {
            throw new EntityNotFoundException("Meeting not found with id: " + id);
        }
        meetingDao.deleteById(id);
    }
}
