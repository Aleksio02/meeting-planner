package ru.epta.notification.dao;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.epta.notification.dao.dto.NotificationDto;

public interface NotificationDao extends JpaRepository<NotificationDto, Long> {
}
