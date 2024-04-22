package com.mintyfinance.domain.transactionhistory;

import java.math.BigDecimal;

public class MonthlyIncomeExpense {
    private BigDecimal totalIncome;
    private BigDecimal totalExpense;

    public MonthlyIncomeExpense(BigDecimal totalIncome, BigDecimal totalExpense) {
        this.totalIncome = totalIncome;
        this.totalExpense = totalExpense;
    }

    public BigDecimal getTotalIncome() {
        return totalIncome;
    }

    public void setTotalIncome(BigDecimal totalIncome) {
        this.totalIncome = totalIncome;
    }

    public BigDecimal getTotalExpense() {
        return totalExpense;
    }

    public void setTotalExpense(BigDecimal totalExpense) {
        this.totalExpense = totalExpense;
    }
}
