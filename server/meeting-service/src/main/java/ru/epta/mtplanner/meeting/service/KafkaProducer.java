package ru.epta.mtplanner.meeting.service;

import java.util.concurrent.CompletableFuture;
import org.apache.kafka.clients.admin.NewTopic;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.SendResult;
import org.springframework.stereotype.Service;
import ru.epta.mtplanner.commons.model.notification.Notification;

@Service
public class KafkaProducer {

    @Autowired
    private NewTopic topic;

    @Autowired
    private KafkaTemplate<String, Object> kafkaTemplate;

    public void sendRequest(Notification request) {

        CompletableFuture<SendResult<String, Object>> future = kafkaTemplate.send(topic.name(), request);

        future.whenComplete((result, ex) -> {
            if (ex == null) {
                System.out.println("Request sent successfully");
                System.out.println("Request topic: " + result.getProducerRecord().topic());
                System.out.println("Request key: " + result.getProducerRecord().key());
                System.out.println("Request value: " + result.getProducerRecord().value());
            } else {
                System.out.println("Error occurred due to sending request: " + ex.getMessage());
            }
        });
    }

}
