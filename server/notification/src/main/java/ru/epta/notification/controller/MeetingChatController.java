package ru.epta.notification.controller;

import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;

// TODO: Delete me
@Controller
public class MeetingChatController {

    private final SimpMessagingTemplate simpMessagingTemplate;

    public MeetingChatController(SimpMessagingTemplate simpMessagingTemplate) {
        this.simpMessagingTemplate = simpMessagingTemplate;
    }

    @MessageMapping("meeting.send")
    public void sendToMeeting() {
        simpMessagingTemplate.convertAndSend(
            "/meeting/%s".formatted("121"),
            "Some message"
        );
    }
}
