package com.mintyfinance.domain.position.dto;

import com.mintyfinance.domain.position.RecurrenceType;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;

public class PositionDto {
    private Long positionId;
    private String category;
    private Long userId;
    private String name;
    private String description;
    private boolean isIncome;
    private byte priority;
    private BigDecimal amount;
    private LocalDateTime rebillDateTime;
    private RecurrenceType recurrenceType;

    public PositionDto(Long positionId, String category, Long userId, String name, String description, boolean isIncome,
                       byte priority, BigDecimal amount, LocalDateTime rebillDateTime, RecurrenceType recurrenceType) {
        this.positionId = positionId;
        this.category = category;
        this.userId = userId;
        this.name = name;
        this.description = description;
        this.isIncome = isIncome;
        this.priority = priority;
        this.amount = amount;
        this.rebillDateTime = rebillDateTime;
        this.recurrenceType = recurrenceType;
    }

    public Long getPositionId() {
        return positionId;
    }

    public void setPositionId(Long positionId) {
        this.positionId = positionId;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public boolean getIsIncome() {
        return isIncome;
    }

    public void setIsIncome(boolean income) {
        isIncome = income;
    }

    public byte getPriority() {
        return priority;
    }

    public void setPriority(byte priority) {
        this.priority = priority;
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public void setAmount(BigDecimal amount) {
        this.amount = amount;
    }

    public String getFormattedRebillDateTime() {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm");
        return rebillDateTime.format(formatter);
    }

    public boolean isInactive() {
        LocalDateTime inactiveDateTime = LocalDateTime.of(9999, 12, 31, 23, 59, 0);
        return this.rebillDateTime.isEqual(inactiveDateTime);
    }

    public void setRebillDateTime(LocalDateTime rebillDateTime) {
        this.rebillDateTime = rebillDateTime;
    }

    public LocalDate getRebillDate() {
        return this.rebillDateTime.toLocalDate();
    }

    public LocalTime getRebillTime() {
        return this.rebillDateTime.toLocalTime();
    }

    public RecurrenceType getRecurrenceType() {
        return recurrenceType;
    }

    public void setRecurrenceType(RecurrenceType recurrenceType) {
        this.recurrenceType = recurrenceType;
    }
}
