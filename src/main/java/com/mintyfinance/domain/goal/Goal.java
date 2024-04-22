package com.mintyfinance.domain.goal;

import com.mintyfinance.domain.user.User;

import javax.persistence.*;
import javax.validation.constraints.*;
import java.math.BigDecimal;

@Entity
@Table(name = "goal")
public class Goal {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "goal_id")
    private Long goalId;
    @ManyToOne
    @JoinColumn(name = "users_id", referencedColumnName = "users_id")
    private User user;
    @NotBlank @Size(max = 100)
    private String name;
    @NotNull
    @DecimalMin(value = "0.1", inclusive = false)
    @Digits(integer = 7, fraction = 2)
    private BigDecimal amount;
    private boolean isCompleted;

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
