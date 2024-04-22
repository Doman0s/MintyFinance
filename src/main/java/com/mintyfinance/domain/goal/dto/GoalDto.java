package com.mintyfinance.domain.goal.dto;

import com.mintyfinance.domain.user.User;

import javax.validation.constraints.*;
import java.math.BigDecimal;


public class GoalDto {
    private Long goalId;
    private User user;
    @NotBlank @Size(max = 100)
    private String name;
    @NotNull
    @DecimalMin(value = "0.1", inclusive = false)
    @Digits(integer = 7, fraction = 2)
    private BigDecimal amount;
    private boolean isCompleted;

    public GoalDto() {
    }

    public GoalDto(Long goalId, User user, String name, BigDecimal amount, boolean isCompleted) {
        this.goalId = goalId;
        this.user = user;
        this.name = name;
        this.amount = amount;
        this.isCompleted = isCompleted;
    }

    public Long getGoalId() {
        return goalId;
    }

    public void setGoalId(Long goalId) {
        this.goalId = goalId;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public void setAmount(BigDecimal amount) {
        this.amount = amount;
    }

    public boolean isCompleted() {
        return isCompleted;
    }

    public void setCompleted(boolean completed) {
        isCompleted = completed;
    }
}
