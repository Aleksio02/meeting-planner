package ru.epta.mtplanner.meeting.service;

import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import ru.epta.mtplanner.commons.dao.UserDao;
import ru.epta.mtplanner.commons.dao.dto.UserDto;
import ru.epta.mtplanner.commons.exception.AccessForbiddenException;
import ru.epta.mtplanner.meeting.dao.MeetingDao;
import ru.epta.mtplanner.meeting.dao.dto.MeetingDto;
import ru.epta.mtplanner.meeting.model.Meeting;
import ru.epta.mtplanner.meeting.model.enums.MeetingStatus;
import ru.epta.mtplanner.meeting.model.request.CreateMeetingRequest;
import ru.epta.mtplanner.meeting.model.request.GetListMeetingRequest;
import ru.epta.mtplanner.meeting.model.request.UpdateMeetingRequest;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class MeetingServiceImplTest {

    @Mock
    MeetingDao meetingDao;

    @Mock
    UserDao userDao;

    @Mock
    NotificationKafkaProducer notificationKafkaProducer;

    @InjectMocks
    MeetingServiceImpl meetingService;

    private UserDto buildUserDto(UUID id, String username) {
        UserDto dto = new UserDto();
        dto.setId(id);
        dto.setUsername(username);
        dto.setEmail(username + "@test.com");
        dto.setPassword("secret");
        return dto;
    }

    private MeetingDto buildMeetingDto(UUID id, String title, UserDto owner) {
        MeetingDto dto = new MeetingDto();
        dto.setId(id);
        dto.setTitle(title);
        dto.setDescription("desc");
        dto.setOwnerId(owner);
        dto.setStartsAt(LocalDateTime.of(2026, 5, 1, 10, 0));
        dto.setDuration(60);
        dto.setStatus(MeetingStatus.PLANNED);
        return dto;
    }

    private CreateMeetingRequest buildCreateRequest(String title, String description,
                                                    LocalDateTime startsAt, Integer duration, MeetingStatus status) {
        CreateMeetingRequest request = new CreateMeetingRequest();
        request.setTitle(title);
        request.setDescription(description);
        request.setStartsAt(startsAt);
        request.setDuration(duration);
        request.setStatus(status);
        return request;
    }

    @Test
    void testGetMeetingById_Success() {
        UUID id = UUID.randomUUID();
        UserDto owner = buildUserDto(UUID.randomUUID(), "Bob");
        MeetingDto dto = buildMeetingDto(id, "Daily", owner);

        when(meetingDao.findById(id)).thenReturn(Optional.of(dto));

        Meeting result = meetingService.getMeetingById(id);

        assertNotNull(result);
        assertEquals(id, result.getId());
        assertEquals("Daily", result.getTitle());
        assertEquals(MeetingStatus.PLANNED.getStatus(), result.getStatus());
        verify(meetingDao).findById(id);
        verifyNoMoreInteractions(meetingDao, userDao);
    }

    @Test
    void testGetMeetingById_NotFound() {
        UUID id = UUID.randomUUID();

        when(meetingDao.findById(id)).thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class, () -> meetingService.getMeetingById(id));

        verify(meetingDao).findById(id);
        verifyNoMoreInteractions(meetingDao, userDao);
    }

    @Test
    void testGetListMeeting_Success() {
        UserDto owner = buildUserDto(UUID.randomUUID(), "Bob");
        MeetingDto dto = buildMeetingDto(UUID.randomUUID(), "Retrospective", owner);
        Page<MeetingDto> page = new PageImpl<>(List.of(dto));
        GetListMeetingRequest request = new GetListMeetingRequest();

        when(meetingDao.findAll(any(Specification.class), any(Pageable.class))).thenReturn(page);

        List<Meeting> result = meetingService.getListMeeting(request);

        assertEquals(1, result.size());
        assertEquals("Retrospective", result.getFirst().getTitle());
        verify(meetingDao).findAll(any(Specification.class), any(Pageable.class));
        verifyNoMoreInteractions(meetingDao, userDao);
    }

    @Test
    void testGetListMeeting_NullRequest() {
        Page<MeetingDto> page = new PageImpl<>(List.of());

        when(meetingDao.findAll(any(Specification.class), any(Pageable.class))).thenReturn(page);

        List<Meeting> result = meetingService.getListMeeting(null);

        assertNotNull(result);
        assertTrue(result.isEmpty());
        verify(meetingDao).findAll(any(Specification.class), any(Pageable.class));
        verifyNoMoreInteractions(meetingDao, userDao);
    }

    @Test
    void testGetListMeeting_EmptyResult() {
        Page<MeetingDto> emptyPage = new PageImpl<>(List.of());
        GetListMeetingRequest request = new GetListMeetingRequest();

        when(meetingDao.findAll(any(Specification.class), any(Pageable.class))).thenReturn(emptyPage);

        List<Meeting> result = meetingService.getListMeeting(request);

        assertNotNull(result);
        assertTrue(result.isEmpty());
        verify(meetingDao).findAll(any(Specification.class), any(Pageable.class));
        verifyNoMoreInteractions(meetingDao, userDao);
    }

    @Test
    void testCreateMeeting_Success() {
        UUID currentId = UUID.randomUUID();
        UserDto owner = buildUserDto(currentId, "Bob");
        CreateMeetingRequest request = buildCreateRequest("Sprint", "Sprint planning",
                LocalDateTime.of(2026, 6, 1, 9, 0), 90, MeetingStatus.PLANNED);
        MeetingDto savedDto = buildMeetingDto(UUID.randomUUID(), "Sprint", owner);

        when(userDao.findById(currentId)).thenReturn(Optional.of(owner));
        when(meetingDao.save(any(MeetingDto.class))).thenReturn(savedDto);

        Meeting result = meetingService.createMeeting(request, currentId);

        assertNotNull(result);
        assertEquals("Sprint", result.getTitle());
        assertEquals(owner.getId(), result.getOwner().getId());
        verify(userDao).findById(currentId);
        verify(meetingDao).save(any(MeetingDto.class));
        verifyNoMoreInteractions(meetingDao, userDao);
    }

    @Test
    void testCreateMeeting_UserNotFound() {
        UUID currentId = UUID.randomUUID();
        CreateMeetingRequest request = buildCreateRequest("Sprint", null, null, 0, MeetingStatus.PLANNED);

        when(userDao.findById(currentId)).thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class, () -> meetingService.createMeeting(request, currentId));

        verify(userDao).findById(currentId);
        verifyNoMoreInteractions(meetingDao, userDao);
    }

    @Test
    void testDeleteMeeting_Success() {
        UUID ownerId = UUID.randomUUID();
        UUID meetingId = UUID.randomUUID();
        UserDto owner = buildUserDto(ownerId, "Bob");
        MeetingDto dto = buildMeetingDto(meetingId, "Daily", owner);

        when(meetingDao.findById(meetingId)).thenReturn(Optional.of(dto));

        meetingService.deleteMeeting(meetingId, ownerId);

        verify(meetingDao).findById(meetingId);
        verify(meetingDao).deleteById(meetingId);
        verifyNoMoreInteractions(meetingDao, userDao);
    }

    @Test
    void testDeleteMeeting_NotFound() {
        UUID meetingId = UUID.randomUUID();
        UUID anyUserId = UUID.randomUUID();

        when(meetingDao.findById(meetingId)).thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class, () -> meetingService.deleteMeeting(meetingId, anyUserId));

        verify(meetingDao).findById(meetingId);
        verifyNoMoreInteractions(meetingDao, userDao);
    }

    @Test
    void testDeleteMeeting_Forbidden() {
        UUID ownerId = UUID.randomUUID();
        UUID currentUserId = UUID.randomUUID();
        UUID meetingId = UUID.randomUUID();
        UserDto owner = buildUserDto(ownerId, "Bob");
        MeetingDto dto = buildMeetingDto(meetingId, "Daily", owner);

        when(meetingDao.findById(meetingId)).thenReturn(Optional.of(dto));

        assertThrows(AccessForbiddenException.class, () -> meetingService.deleteMeeting(meetingId, currentUserId));

        verify(meetingDao).findById(meetingId);
        verify(meetingDao, never()).deleteById(any());
        verifyNoMoreInteractions(meetingDao, userDao);
    }

    @Test
    void testUpdateMeeting_Success() {
        UUID ownerId = UUID.randomUUID();
        UUID meetingId = UUID.randomUUID();
        UserDto owner = buildUserDto(ownerId, "Bob");
        MeetingDto dto = buildMeetingDto(meetingId, "Old Title", owner);
        MeetingDto savedDto = buildMeetingDto(meetingId, "New Title", owner);
        savedDto.setDescription("New desc");
        savedDto.setStatus(MeetingStatus.CANCELED);
        UpdateMeetingRequest request = new UpdateMeetingRequest();
        request.setTitle(Optional.of("New Title"));
        request.setDescription(Optional.of("New desc"));
        request.setStartsAt(Optional.of(LocalDateTime.of(2026, 7, 1, 11, 0)));
        request.setDuration(Optional.of(45));
        request.setStatus(Optional.of(MeetingStatus.CANCELED));

        when(meetingDao.findById(meetingId)).thenReturn(Optional.of(dto));
        when(meetingDao.save(any(MeetingDto.class))).thenReturn(savedDto);

        Meeting result = meetingService.updateMeeting(meetingId, request, ownerId);

        assertNotNull(result);
        assertEquals("New Title", result.getTitle());
        assertEquals(MeetingStatus.CANCELED.getStatus(), result.getStatus());
        verify(meetingDao).findById(meetingId);
        verify(meetingDao).save(any(MeetingDto.class));
        verifyNoMoreInteractions(meetingDao, userDao);
    }

    @Test
    void testUpdateMeeting_NoFieldsUpdated() {
        UUID ownerId = UUID.randomUUID();
        UUID meetingId = UUID.randomUUID();
        UserDto owner = buildUserDto(ownerId, "Bob");
        MeetingDto dto = buildMeetingDto(meetingId, "Planning", owner);
        UpdateMeetingRequest request = new UpdateMeetingRequest();

        when(meetingDao.findById(meetingId)).thenReturn(Optional.of(dto));
        when(meetingDao.save(any(MeetingDto.class))).thenReturn(dto);

        Meeting result = meetingService.updateMeeting(meetingId, request, ownerId);

        assertNotNull(result);
        assertEquals("Planning", result.getTitle());
        verify(meetingDao).findById(meetingId);
        verify(meetingDao).save(dto);
        verifyNoMoreInteractions(meetingDao, userDao);
    }

    @Test
    void testUpdateMeeting_NotFound() {
        UUID meetingId = UUID.randomUUID();
        UpdateMeetingRequest request = new UpdateMeetingRequest();
        UUID anyUserId = UUID.randomUUID();

        when(meetingDao.findById(meetingId)).thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class, () -> meetingService.updateMeeting(meetingId, request, anyUserId));

        verify(meetingDao).findById(meetingId);
        verifyNoMoreInteractions(meetingDao, userDao);
    }

    @Test
    void testUpdateMeeting_Forbidden() {
        UUID ownerId = UUID.randomUUID();
        UUID currentUserId = UUID.randomUUID();
        UUID meetingId = UUID.randomUUID();
        UserDto owner = buildUserDto(ownerId, "Bob");
        MeetingDto dto = buildMeetingDto(meetingId, "Daily", owner);
        UpdateMeetingRequest request = new UpdateMeetingRequest();

        when(meetingDao.findById(meetingId)).thenReturn(Optional.of(dto));

        assertThrows(AccessForbiddenException.class, () -> meetingService.updateMeeting(meetingId, request, currentUserId));

        verify(meetingDao).findById(meetingId);
        verify(meetingDao, never()).save(any());
        verifyNoMoreInteractions(meetingDao, userDao);
    }
}