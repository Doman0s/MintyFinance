package com.mintyfinance.domain.transactionhistory;

import com.mintyfinance.domain.transactionhistory.dto.TransactionHistoryDto;

import java.util.List;

public class MonthlySummary {
    private List<TransactionHistoryDto> transactions;
    private MonthlyIncomeExpense incomeExpenseSummary;

    public MonthlySummary(List<TransactionHistoryDto> transactions, MonthlyIncomeExpense incomeExpenseSummary) {
        this.transactions = transactions;
        this.incomeExpenseSummary = incomeExpenseSummary;
    }

    public List<TransactionHistoryDto> getTransactions() {
        return transactions;
    }

    public void setTransactions(List<TransactionHistoryDto> transactions) {
        this.transactions = transactions;
    }

    public MonthlyIncomeExpense getIncomeExpenseSummary() {
        return incomeExpenseSummary;
    }

    public void setIncomeExpenseSummary(MonthlyIncomeExpense incomeExpenseSummary) {
        this.incomeExpenseSummary = incomeExpenseSummary;
    }
}
