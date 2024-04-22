package com.mintyfinance.domain.exception;

public class CantDeleteCategoryWithPositions extends RuntimeException {
    public CantDeleteCategoryWithPositions(String message) {
        super(message);
    }
}
