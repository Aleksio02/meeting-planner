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
import ru.epta.mtplanner.commons.exception.IncorrectRequestDataException;
import ru.epta.mtplanner.meeting.dao.InviteDao;
import ru.epta.mtplanner.meeting.dao.MeetingDao;
import ru.epta.mtplanner.meeting.dao.dto.InviteDto;
import ru.epta.mtplanner.meeting.dao.dto.MeetingDto;
import ru.epta.mtplanner.meeting.model.Invite;
import ru.epta.mtplanner.meeting.model.enums.InviteStatus;
import ru.epta.mtplanner.meeting.model.enums.MeetingStatus;
import ru.epta.mtplanner.meeting.model.request.CreateInviteRequest;
import ru.epta.mtplanner.meeting.model.request.GetListInviteRequest;

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
class InviteServiceImplTest {

    @Mock
    MeetingDao meetingDao;

    @Mock
    UserDao userDao;

    @Mock
    InviteDao inviteDao;

    @Mock
    NotificationKafkaProducer notificationKafkaProducer;

    @InjectMocks
    InviteServiceImpl inviteService;

    private UserDto buildUserDto(UUID id, String username) {
        UserDto dto = new UserDto();
        dto.setId(id);
        dto.setUsername(username);
        dto.setEmail(username + "@test.com");
        dto.setPassword("secret");
        return dto;
    }

    private MeetingDto buildMeetingDto(UUID id, UserDto owner) {
        MeetingDto dto = new MeetingDto();
        dto.setId(id);
        dto.setTitle("Daily");
        dto.setDescription("desc");
        dto.setOwnerId(owner);
        dto.setStartsAt(LocalDateTime.of(2026, 5, 1, 10, 0));
        dto.setDuration(60);
        dto.setStatus(MeetingStatus.PLANNED);
        return dto;
    }

    private InviteDto buildInviteDto(UUID id, MeetingDto meeting, UserDto user, InviteStatus status) {
        InviteDto dto = new InviteDto();
        dto.setId(id);
        dto.setMeeting(meeting);
        dto.setUser(user);
        dto.setStatus(status);
        dto.setSentAt(LocalDateTime.now());
        return dto;
    }

    private CreateInviteRequest buildCreateInviteRequest(UUID meetingId, UUID userId, InviteStatus status) {
        CreateInviteRequest request = new CreateInviteRequest();
        request.setMeetingId(meetingId);
        request.setUserId(userId);
        request.setStatus(status);
        return request;
    }

    @Test
    void testGetInviteById_Success() {
        UUID bobId = UUID.randomUUID();
        UUID inviteId = UUID.randomUUID();
        UserDto bob = buildUserDto(bobId, "Bob");
        MeetingDto meeting = buildMeetingDto(UUID.randomUUID(), bob);
        InviteDto inviteDto = buildInviteDto(inviteId, meeting, bob, InviteStatus.PENDING);

        when(inviteDao.findById(inviteId)).thenReturn(Optional.of(inviteDto));

        Invite result = inviteService.getInviteById(inviteId);

        assertNotNull(result);
        assertEquals(inviteId, result.getId());

        verify(inviteDao).findById(inviteId);
        verifyNoMoreInteractions(meetingDao, userDao, inviteDao, notificationKafkaProducer);
    }

    @Test
    void testGetInviteById_NotFound() {
        UUID inviteId = UUID.randomUUID();

        when(inviteDao.findById(inviteId)).thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class, () -> inviteService.getInviteById(inviteId));

        verify(inviteDao).findById(inviteId);
        verifyNoMoreInteractions(meetingDao, userDao, inviteDao, notificationKafkaProducer);
    }

    @Test
    void testGetListInviteRequest_Success() {
        UUID bobId = UUID.randomUUID();
        UserDto bob = buildUserDto(bobId, "Bob");
        MeetingDto meeting = buildMeetingDto(UUID.randomUUID(), bob);
        InviteDto inviteDto = buildInviteDto(UUID.randomUUID(), meeting, bob, InviteStatus.PENDING);
        GetListInviteRequest request = new GetListInviteRequest();
        Page<InviteDto> page = new PageImpl<>(List.of(inviteDto));

        when(inviteDao.findAll(any(Specification.class), any(Pageable.class))).thenReturn(page);

        List<Invite> result = inviteService.getListInviteRequest(request);

        assertEquals(1, result.size());
        assertNotNull(result.getFirst());

        verify(inviteDao).findAll(any(Specification.class), any(Pageable.class));
        verifyNoMoreInteractions(meetingDao, userDao, inviteDao, notificationKafkaProducer);
    }

    @Test
    void testGetListInviteRequest_NullRequest() {
        Page<InviteDto> page = new PageImpl<>(List.of());

        when(inviteDao.findAll(any(Specification.class), any(Pageable.class))).thenReturn(page);

        List<Invite> result = inviteService.getListInviteRequest(null);

        assertNotNull(result);

        verify(inviteDao).findAll(any(Specification.class), any(Pageable.class));
        verifyNoMoreInteractions(meetingDao, userDao, inviteDao, notificationKafkaProducer);
    }

    @Test
    void testGetListInviteRequest_EmptyResult() {
        GetListInviteRequest request = new GetListInviteRequest();
        Page<InviteDto> page = new PageImpl<>(List.of());

        when(inviteDao.findAll(any(Specification.class), any(Pageable.class))).thenReturn(page);

        List<Invite> result = inviteService.getListInviteRequest(request);

        assertNotNull(result);
        assertTrue(result.isEmpty());

        verify(inviteDao).findAll(any(Specification.class), any(Pageable.class));
        verifyNoMoreInteractions(meetingDao, userDao, inviteDao, notificationKafkaProducer);
    }

    @Test
    void testCreateInvite_Success() {
        UUID ownerId = UUID.randomUUID();
        UUID guestId = UUID.randomUUID();
        UUID meetingId = UUID.randomUUID();
        UserDto bob = buildUserDto(ownerId, "Bob");
        UserDto maria = buildUserDto(guestId, "Maria");
        MeetingDto meetingDto = buildMeetingDto(meetingId, bob);
        CreateInviteRequest request = buildCreateInviteRequest(meetingId, guestId, InviteStatus.PENDING);
        InviteDto savedInviteDto = buildInviteDto(UUID.randomUUID(), meetingDto, maria, InviteStatus.PENDING);

        when(meetingDao.findById(meetingId)).thenReturn(Optional.of(meetingDto));
        when(userDao.findById(guestId)).thenReturn(Optional.of(maria));
        when(inviteDao.save(any(InviteDto.class))).thenReturn(savedInviteDto);

        Invite result = inviteService.createInvite(request, ownerId);

        assertNotNull(result);

        verify(meetingDao).findById(meetingId);
        verify(userDao).findById(guestId);
        verify(inviteDao).save(any(InviteDto.class));
        verify(notificationKafkaProducer).sendNotification(any());
        verifyNoMoreInteractions(meetingDao, userDao, inviteDao, notificationKafkaProducer);
    }

    @Test
    void testCreateInvite_MeetingNotFound() {
        UUID ownerId = UUID.randomUUID();
        UUID meetingId = UUID.randomUUID();
        CreateInviteRequest request = buildCreateInviteRequest(meetingId, UUID.randomUUID(), InviteStatus.PENDING);

        when(meetingDao.findById(meetingId)).thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class, () -> inviteService.createInvite(request, ownerId));

        verify(meetingDao).findById(meetingId);
        verifyNoMoreInteractions(meetingDao, userDao, inviteDao, notificationKafkaProducer);
    }

    @Test
    void testCreateInvite_Forbidden() {
        UUID ownerId = UUID.randomUUID();
        UUID currentId = UUID.randomUUID();
        UUID meetingId = UUID.randomUUID();
        UserDto bob = buildUserDto(ownerId, "Bob");
        MeetingDto meetingDto = buildMeetingDto(meetingId, bob);
        CreateInviteRequest request = buildCreateInviteRequest(meetingId, UUID.randomUUID(), InviteStatus.PENDING);

        when(meetingDao.findById(meetingId)).thenReturn(Optional.of(meetingDto));

        assertThrows(AccessForbiddenException.class, () -> inviteService.createInvite(request, currentId));

        verify(meetingDao).findById(meetingId);
        verifyNoMoreInteractions(meetingDao, userDao, inviteDao, notificationKafkaProducer);
    }

    @Test
    void testCreateInvite_UserNotFound() {
        UUID ownerId = UUID.randomUUID();
        UUID guestId = UUID.randomUUID();
        UUID meetingId = UUID.randomUUID();
        UserDto bob = buildUserDto(ownerId, "Bob");
        MeetingDto meetingDto = buildMeetingDto(meetingId, bob);
        CreateInviteRequest request = buildCreateInviteRequest(meetingId, guestId, InviteStatus.PENDING);

        when(meetingDao.findById(meetingId)).thenReturn(Optional.of(meetingDto));
        when(userDao.findById(guestId)).thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class, () -> inviteService.createInvite(request, ownerId));

        verify(meetingDao).findById(meetingId);
        verify(userDao).findById(guestId);
        verifyNoMoreInteractions(meetingDao, userDao, inviteDao, notificationKafkaProducer);
    }

    @Test
    void testCreateInvite_InviteOwner() {
        UUID ownerId = UUID.randomUUID();
        UUID meetingId = UUID.randomUUID();
        UserDto bob = buildUserDto(ownerId, "Bob");
        MeetingDto meetingDto = buildMeetingDto(meetingId, bob);
        CreateInviteRequest request = buildCreateInviteRequest(meetingId, ownerId, InviteStatus.PENDING);

        when(meetingDao.findById(meetingId)).thenReturn(Optional.of(meetingDto));
        when(userDao.findById(ownerId)).thenReturn(Optional.of(bob));

        assertThrows(IncorrectRequestDataException.class, () -> inviteService.createInvite(request, ownerId));

        verify(meetingDao).findById(meetingId);
        verify(userDao).findById(ownerId);
        verifyNoMoreInteractions(meetingDao, userDao, inviteDao, notificationKafkaProducer);
    }

    @Test
    void testDeleteInvite_Success() {
        UUID ownerId = UUID.randomUUID();
        UUID inviteId = UUID.randomUUID();
        UserDto bob = buildUserDto(ownerId, "Bob");
        UserDto maria = buildUserDto(UUID.randomUUID(), "Maria");
        MeetingDto meetingDto = buildMeetingDto(UUID.randomUUID(), bob);
        InviteDto inviteDto = buildInviteDto(inviteId, meetingDto, maria, InviteStatus.PENDING);

        when(inviteDao.findById(inviteId)).thenReturn(Optional.of(inviteDto));

        inviteService.deleteInvite(inviteId, ownerId);

        verify(inviteDao).findById(inviteId);
        verify(inviteDao).deleteById(inviteId);
        verifyNoMoreInteractions(meetingDao, userDao, inviteDao, notificationKafkaProducer);
    }

    @Test
    void testDeleteInvite_NotFound() {
        UUID inviteId = UUID.randomUUID();
        UUID anyUserId = UUID.randomUUID();

        when(inviteDao.findById(inviteId)).thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class, () -> inviteService.deleteInvite(inviteId, anyUserId));

        verify(inviteDao).findById(inviteId);
        verifyNoMoreInteractions(meetingDao, userDao, inviteDao, notificationKafkaProducer);
    }

    @Test
    void testDeleteInvite_Forbidden() {
        UUID ownerId = UUID.randomUUID();
        UUID currentUserId = UUID.randomUUID();
        UUID inviteId = UUID.randomUUID();
        UserDto bob = buildUserDto(ownerId, "Bob");
        UserDto maria = buildUserDto(UUID.randomUUID(), "Maria");
        MeetingDto meetingDto = buildMeetingDto(UUID.randomUUID(), bob);
        InviteDto inviteDto = buildInviteDto(inviteId, meetingDto, maria, InviteStatus.PENDING);

        when(inviteDao.findById(inviteId)).thenReturn(Optional.of(inviteDto));

        assertThrows(AccessForbiddenException.class, () -> inviteService.deleteInvite(inviteId, currentUserId));

        verify(inviteDao).findById(inviteId);
        verify(inviteDao, never()).deleteById(any());
        verifyNoMoreInteractions(meetingDao, userDao, inviteDao, notificationKafkaProducer);
    }

    @Test
    void testUpdateInvite_Success_Accepted() {
        UUID mariaId = UUID.randomUUID();
        UUID inviteId = UUID.randomUUID();
        UserDto bob = buildUserDto(UUID.randomUUID(), "Bob");
        UserDto maria = buildUserDto(mariaId, "Maria");
        MeetingDto meetingDto = buildMeetingDto(UUID.randomUUID(), bob);
        InviteDto inviteDto = buildInviteDto(inviteId, meetingDto, maria, InviteStatus.PENDING);
        InviteDto savedInviteDto = buildInviteDto(inviteId, meetingDto, maria, InviteStatus.ACCEPTED);

        when(inviteDao.findById(inviteId)).thenReturn(Optional.of(inviteDto));
        when(inviteDao.save(any(InviteDto.class))).thenReturn(savedInviteDto);

        Invite result = inviteService.updateInvite(inviteId, InviteStatus.ACCEPTED, mariaId);

        assertNotNull(result);
        assertEquals(InviteStatus.ACCEPTED.getStatus(), result.getStatus());

        verify(inviteDao).findById(inviteId);
        verify(inviteDao).save(any(InviteDto.class));
        verify(notificationKafkaProducer).sendNotification(any());
        verifyNoMoreInteractions(meetingDao, userDao, inviteDao, notificationKafkaProducer);
    }

    @Test
    void testUpdateInvite_Success_Declined() {
        UUID mariaId = UUID.randomUUID();
        UUID inviteId = UUID.randomUUID();
        UserDto bob = buildUserDto(UUID.randomUUID(), "Bob");
        UserDto maria = buildUserDto(mariaId, "Maria");
        MeetingDto meetingDto = buildMeetingDto(UUID.randomUUID(), bob);
        InviteDto inviteDto = buildInviteDto(inviteId, meetingDto, maria, InviteStatus.PENDING);
        InviteDto savedInviteDto = buildInviteDto(inviteId, meetingDto, maria, InviteStatus.DECLINED);

        when(inviteDao.findById(inviteId)).thenReturn(Optional.of(inviteDto));
        when(inviteDao.save(any(InviteDto.class))).thenReturn(savedInviteDto);

        Invite result = inviteService.updateInvite(inviteId, InviteStatus.DECLINED, mariaId);

        assertNotNull(result);
        assertEquals(InviteStatus.DECLINED.getStatus(), result.getStatus());

        verify(inviteDao).findById(inviteId);
        verify(inviteDao).save(any(InviteDto.class));
        verify(notificationKafkaProducer).sendNotification(any());
        verifyNoMoreInteractions(meetingDao, userDao, inviteDao, notificationKafkaProducer);
    }

    @Test
    void testUpdateInvite_NotFound() {
        UUID inviteId = UUID.randomUUID();
        UUID anyUserId = UUID.randomUUID();

        when(inviteDao.findById(inviteId)).thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class, () -> inviteService.updateInvite(inviteId, InviteStatus.ACCEPTED, anyUserId));

        verify(inviteDao).findById(inviteId);
        verifyNoMoreInteractions(meetingDao, userDao, inviteDao, notificationKafkaProducer);
    }

    @Test
    void testUpdateInvite_Forbidden() {
        UUID mariaId = UUID.randomUUID();
        UUID currentUserId = UUID.randomUUID();
        UUID inviteId = UUID.randomUUID();
        UserDto bob = buildUserDto(UUID.randomUUID(), "Bob");
        UserDto maria = buildUserDto(mariaId, "Maria");
        MeetingDto meetingDto = buildMeetingDto(UUID.randomUUID(), bob);
        InviteDto inviteDto = buildInviteDto(inviteId, meetingDto, maria, InviteStatus.PENDING);

        when(inviteDao.findById(inviteId)).thenReturn(Optional.of(inviteDto));

        assertThrows(AccessForbiddenException.class, () -> inviteService.updateInvite(inviteId, InviteStatus.ACCEPTED, currentUserId));

        verify(inviteDao).findById(inviteId);
        verify(inviteDao, never()).save(any());
        verifyNoMoreInteractions(meetingDao, userDao, inviteDao, notificationKafkaProducer);
    }
}
