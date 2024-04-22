package com.mintyfinance.domain.position.dto;

import com.mintyfinance.domain.position.RecurrenceType;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.multipart.MultipartFile;

import javax.validation.constraints.*;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

public class PositionSaveDto {
    private Long positionId;
    @NotNull
    private String category;
    private Long userId;
    @NotBlank @Size(max = 100)
    private String name;
    @NotBlank @Size(max = 500)
    private String description;
    private boolean isIncome;
    @Min(1) @Max(10)
    private byte priority;
    @NotNull
    @DecimalMin(value = "0", inclusive = false)
    @Digits(integer = 7, fraction = 2)
    private BigDecimal amount;
    @DateTimeFormat(pattern = "yyyy-MM-dd") @NotNull
    private LocalDate rebillDate;
    @NotNull
    private LocalTime rebillTime;
    @NotNull
    private RecurrenceType recurrenceType;

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

    public LocalDate getRebillDate() {
        return rebillDate;
    }

    public void setRebillDate(LocalDate rebillDate) {
        this.rebillDate = rebillDate;
    }

    public LocalTime getRebillTime() {
        return rebillTime;
    }

    public void setRebillTime(LocalTime rebillTime) {
        this.rebillTime = rebillTime;
    }

    public RecurrenceType getRecurrenceType() {
        return recurrenceType;
    }

    public void setRecurrenceType(RecurrenceType recurrenceType) {
        this.recurrenceType = recurrenceType;
    }
}
