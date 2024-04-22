package com.mintyfinance.domain.position;

public enum RecurrenceType {
    ONCE("Jednorazowo"),
    DAILY("Codziennie"),
    WEEKLY("Co tydzień"),
    BIWEEKLY("Co 2 tygodnie"),
    MONTHLY("Co miesiąc"),
    BIMONTHLY("Co 2 miesiące"),
    SEMI_ANNUALLY("Co pół roku"),
    ANNUALLY("Co rok");

    private final String description;

    RecurrenceType(String description) {
        this.description = description;
    }

    public String getDescription() {
        return description;
    }
}
