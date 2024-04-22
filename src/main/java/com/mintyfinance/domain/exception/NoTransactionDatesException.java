package com.mintyfinance.domain.exception;

public class NoTransactionDatesException extends RuntimeException {
    public NoTransactionDatesException(String message) {
        super(message);
    }
}

