package ru.epta.mtplanner.meeting.model.enums;

public enum InviteStatus {
    ACCEPTED("Принято"),
    DECLINED("Отклонено"),
    PENDING("Ожидает");

    private final String status;

    InviteStatus(String status) {
        this.status = status;
    }

    public String getStatus() {
        return status;
    }
}
