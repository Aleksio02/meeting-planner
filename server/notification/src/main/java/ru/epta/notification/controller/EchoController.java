package ru.epta.notification.controller;

import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.stereotype.Controller;

@Controller
public class EchoController {


    @MessageMapping("/echo")
    @SendTo("/topic/reply")
    public String echo(String message) {
        return "Echo: " + message;
    }
}
