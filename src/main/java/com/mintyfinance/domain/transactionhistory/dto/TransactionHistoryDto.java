package com.mintyfinance.domain.transactionhistory.dto;

import com.mintyfinance.domain.position.Position;
import com.mintyfinance.domain.user.User;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class TransactionHistoryDto {
    private Long transactionHistoryId;
    private User user;
    private Position position;
    private LocalDateTime transactionDate;
    private BigDecimal amount;
    private BigDecimal balance_before_transaction;
    private BigDecimal balance_after_transaction;

    public TransactionHistoryDto(Long transactionHistoryId, User user, Position position, LocalDateTime transactionDate,
                                 BigDecimal amount, BigDecimal balance_before_transaction,
                                 BigDecimal balance_after_transaction) {
        this.transactionHistoryId = transactionHistoryId;
        this.user = user;
        this.position = position;
        this.transactionDate = transactionDate;
        this.amount = amount;
        this.balance_before_transaction = balance_before_transaction;
        this.balance_after_transaction = balance_after_transaction;
    }

    public Long getTransactionHistoryId() {
        return transactionHistoryId;
    }

    public void setTransactionHistoryId(Long transactionHistoryId) {
        this.transactionHistoryId = transactionHistoryId;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public Position getPosition() {
        return position;
    }

    public void setPosition(Position position) {
        this.position = position;
    }

    public LocalDateTime getTransactionDate() {
        return transactionDate;
    }

    public String getFormattedTransactionDate() {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm");
        return transactionDate.format(formatter);
    }

    public void setTransactionDate(LocalDateTime transactionDate) {
        this.transactionDate = transactionDate;
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public void setAmount(BigDecimal amount) {
        this.amount = amount;
    }

    public BigDecimal getBalance_before_transaction() {
        return balance_before_transaction;
    }

    public void setBalance_before_transaction(BigDecimal balance_before_transaction) {
        this.balance_before_transaction = balance_before_transaction;
    }

    public BigDecimal getBalance_after_transaction() {
        return balance_after_transaction;
    }

    public void setBalance_after_transaction(BigDecimal balance_after_transaction) {
        this.balance_after_transaction = balance_after_transaction;
    }
}
