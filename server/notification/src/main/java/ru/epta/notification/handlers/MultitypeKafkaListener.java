package ru.epta.notification.handlers;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.kafka.annotation.KafkaHandler;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Component;
import ru.epta.mtplanner.commons.model.notification.SendInviteNotification;

@Component
@KafkaListener(id = "${spring.kafka.group-id}", topics = "${spring.kafka.primary-topic-name}", containerFactory = "kafkaListenerContainerFactory")
public class MultitypeKafkaListener {

    private final SimpMessagingTemplate simpMessagingTemplate;

    public MultitypeKafkaListener(SimpMessagingTemplate simpMessagingTemplate) {
        this.simpMessagingTemplate = simpMessagingTemplate;
    }

    @KafkaHandler
    public void handleSendInvite(SendInviteNotification request) throws JsonProcessingException {
        System.out.println("Processing notification");
        System.out.println(new ObjectMapper().writeValueAsString(request));
        // TODO: Доработать
        simpMessagingTemplate.convertAndSend("/notification/%s".formatted("1"), new ObjectMapper().writeValueAsString(request));
    }


    @KafkaHandler(isDefault = true)
    public void defaultHandler(Object object) {
        throw new IllegalStateException("Unidentified object received");
    }
}
