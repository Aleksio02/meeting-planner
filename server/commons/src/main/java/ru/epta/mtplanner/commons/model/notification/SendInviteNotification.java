package ru.epta.mtplanner.commons.model.notification;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class SendInviteNotification extends Notification {
    private String message;
}
