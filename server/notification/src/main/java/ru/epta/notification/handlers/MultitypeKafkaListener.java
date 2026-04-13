package ru.epta.notification.handlers;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.kafka.annotation.KafkaHandler;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;
import ru.epta.mtplanner.commons.model.notification.Notification;
import ru.epta.notification.service.NotificationService;

@Component
@KafkaListener(id = "${spring.kafka.group-id}", topics = "${spring.kafka.primary-topic-name}", containerFactory = "kafkaListenerContainerFactory")
public class MultitypeKafkaListener {

    private NotificationService notificationService;

    public MultitypeKafkaListener(NotificationService notificationService) {
        this.notificationService = notificationService;
    }

    @KafkaHandler
    public void handleSendInvite(Notification request) throws JsonProcessingException {
        System.out.println("Processing notification");
        notificationService.sendNotification(request);
    }


    @KafkaHandler(isDefault = true)
    public void defaultHandler(Object object) {
        throw new IllegalStateException("Unidentified object received");
    }
}
