package ru.epta.mtplanner.commons.exception;

public class ServerUnavailableException extends RuntimeException {
    public ServerUnavailableException(String message) {
        super(message);
    }
}
