package com.mintyfinance.domain.exception;

public class UserNotFoundForPasswordResetException extends RuntimeException {
    public UserNotFoundForPasswordResetException(String message) {
        super(message);
    }
}
