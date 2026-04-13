package ru.epta.notification.service;

import java.util.UUID;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;
import ru.epta.mtplanner.commons.model.notification.Notification;
import ru.epta.notification.converter.NotificationConverter;
import ru.epta.notification.dao.NotificationDao;

@Service
public class NotificationServiceImpl implements NotificationService {

    private final String DESTINATION_PATH_PATTERN = "/notification/%s";

    private final SimpMessagingTemplate simpMessagingTemplate;

    private final NotificationDao notificationDao;

    private final NotificationConverter notificationConverter;

    public NotificationServiceImpl(
        SimpMessagingTemplate simpMessagingTemplate,
        NotificationDao notificationDao,
        NotificationConverter notificationConverter
    ) {
        this.simpMessagingTemplate = simpMessagingTemplate;
        this.notificationDao = notificationDao;
        this.notificationConverter = notificationConverter;
    }


    @Override
    public void sendNotification(Notification notification) {
        for (UUID id : notification.extractReceivers()) {
            notificationDao.save(notificationConverter.toDto(notification, id));

            ExecutorService executorService = Executors.newSingleThreadExecutor();
            executorService.submit(() -> {
                try {
                    simpMessagingTemplate.convertAndSend(
                        DESTINATION_PATH_PATTERN.formatted(id.toString()),
                        notification
                    );
                } catch (Exception e) {
                    throw new RuntimeException(e);
                }
            });
        }
    }
}
