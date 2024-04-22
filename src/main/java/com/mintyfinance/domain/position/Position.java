package com.mintyfinance.domain.position;

import com.mintyfinance.domain.category.Category;
import com.mintyfinance.domain.user.User;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;

import javax.persistence.*;
import javax.validation.constraints.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "position")
@NoArgsConstructor
@AllArgsConstructor
public class Position {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "position_id")
    private Long positionId;
    @ManyToOne
    @JoinColumn(name = "category_id", referencedColumnName = "category_id")
    @NotNull
    private Category category;
    @ManyToOne
    @JoinColumn(name = "users_id", referencedColumnName = "users_id")
    private User user;
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
    @NotNull
    private LocalDateTime rebillDate;
    @Column(name = "recurrence_type")
    @Enumerated(EnumType.STRING)
    @NotNull
    private RecurrenceType recurrenceType;

    public Long getPositionId() {
        return positionId;
    }

    public void setPositionId(Long positionId) {
        this.positionId = positionId;
    }

    public Category getCategory() {
        return category;
    }

    public void setCategory(Category category) {
        this.category = category;
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

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public boolean isIncome() {
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

    public LocalDateTime getRebillDate() {
        return rebillDate;
    }

    public void setRebillDate(LocalDateTime rebillDate) {
        this.rebillDate = rebillDate;
    }

    public RecurrenceType getRecurrenceType() {
        return recurrenceType;
    }

    public void setRecurrenceType(RecurrenceType recurrenceType) {
        this.recurrenceType = recurrenceType;
    }
}
