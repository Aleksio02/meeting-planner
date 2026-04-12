package ru.epta.notification.service;


import com.fasterxml.jackson.core.JsonProcessingException;
import ru.epta.mtplanner.commons.model.notification.Notification;

public interface NotificationService {
    void sendNotification(Notification notification) throws JsonProcessingException;
}
