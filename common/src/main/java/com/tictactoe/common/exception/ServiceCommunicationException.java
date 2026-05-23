package com.tictactoe.common.exception;

public class ServiceCommunicationException extends RuntimeException {
    public ServiceCommunicationException(String message, Throwable cause) {
        super(message, cause);
    }

    public ServiceCommunicationException(String message) {
        super(message);
    }
}
