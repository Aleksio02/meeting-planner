package ru.epta.mtplanner.meeting.model.enums;

public enum MeetingStatus {
    PLANNED("Запланирована"),
    CANCELED("Отменена"),
    COMPLETED("Завершена"),
    POSTPONED("Перенесена");

    private final String status;

    MeetingStatus(String status) {
        this.status = status;
    }

    public String getStatus() {
        return status;
    }
}
