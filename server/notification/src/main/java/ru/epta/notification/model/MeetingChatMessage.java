package ru.epta.notification.model;

import lombok.Data;

@Data
public class MeetingChatMessage {
    private String meetingId;
    private String sender;
    private String message;
}
