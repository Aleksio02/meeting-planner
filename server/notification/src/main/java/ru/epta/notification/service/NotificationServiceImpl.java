package ru.epta.notification.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.UUID;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;
import ru.epta.mtplanner.commons.model.notification.Notification;

@Service
public class NotificationServiceImpl implements NotificationService {

    private final String DESTINATION_PATH_PATTERN = "/notification/%s";

    private final SimpMessagingTemplate simpMessagingTemplate;

    public NotificationServiceImpl(SimpMessagingTemplate simpMessagingTemplate) {this.simpMessagingTemplate = simpMessagingTemplate;}


    @Override
    public void sendNotification(Notification notification) throws JsonProcessingException {

        for (UUID id: notification.extractReceivers()) {
            simpMessagingTemplate.convertAndSend(
                DESTINATION_PATH_PATTERN.formatted(id.toString()),
                new ObjectMapper().writeValueAsString(notification)
            );
        }
    }
}
