package com.phoebus.communitycentersapi.exception.utils.httpException;

public class NotFoundException extends RuntimeException {
    public NotFoundException(String message) {
        super(message);
    }
}