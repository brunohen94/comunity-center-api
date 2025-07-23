package com.phoebus.communitycentersapi.exception.utils.httpException;

public class ConflictException extends RuntimeException {
    public ConflictException(String message) {
        super(message);
    }
}