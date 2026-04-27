package ru.epta.notification.dao.dto;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.time.LocalDateTime;
import java.util.UUID;
import lombok.Getter;
import lombok.Setter;
import ru.epta.mtplanner.commons.model.notification.NotificationType;

@Getter
@Setter
@Entity
@Table(schema = "public", name = "notifications")
public class NotificationDto {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @Column(columnDefinition = "uuid", nullable = false)
    private UUID actorId;

    @Column(columnDefinition = "uuid")
    private UUID receiverId;

    @Column(columnDefinition = "uuid")
    private UUID meetingId;

    @Column(columnDefinition = "uuid")
    private UUID inviteId;

    @Column
    @Enumerated(EnumType.STRING)
    private NotificationType type;

    @Column
    private String comment;

    @Column(name = "sent_at")
    private LocalDateTime sentAt;
}
