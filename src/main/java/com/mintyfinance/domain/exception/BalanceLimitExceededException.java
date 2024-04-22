package com.mintyfinance.domain.exception;

public class BalanceLimitExceededException extends RuntimeException {
    public BalanceLimitExceededException(String message) {
        super(message);
    }
}