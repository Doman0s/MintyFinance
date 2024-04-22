package com.mintyfinance.domain.transactionhistory;

import com.mintyfinance.domain.transactionhistory.dto.TransactionHistoryDto;

class TransactionHistoryDtoMapper {
    static TransactionHistoryDto map(TransactionHistory transactionHistory) {
        return new TransactionHistoryDto(
                transactionHistory.getTransactionHistoryId(),
                transactionHistory.getUser(),
                transactionHistory.getPosition(),
                transactionHistory.getTransactionDate(),
                transactionHistory.getAmount(),
                transactionHistory.getBalanceBeforeTransaction(),
                transactionHistory.getBalanceAfterTransaction()
        );
    }
}
