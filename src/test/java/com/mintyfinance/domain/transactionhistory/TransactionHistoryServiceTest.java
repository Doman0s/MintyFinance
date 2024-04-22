package com.mintyfinance.domain.transactionhistory;

import com.mintyfinance.domain.position.Position;
import com.mintyfinance.domain.user.User;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class TransactionHistoryServiceTest {
    @Mock
    private TransactionHistoryRepository transactionHistoryRepository;

    @InjectMocks
    private TransactionHistoryService transactionHistoryService;

    TransactionHistoryServiceTest() {
    }

    @Test
    void testAddEntryToHistory() {
        // Przygotowanie danych
        User user = new User();
        Position position = new Position();
        position.setUser(user);
        BigDecimal amount = BigDecimal.valueOf(100.00);
        LocalDateTime transactionDate = LocalDateTime.now();
        BigDecimal balanceBefore = BigDecimal.valueOf(1000.00);
        BigDecimal balanceAfter = balanceBefore.add(amount);

        // Akcja
        transactionHistoryService.addEntryToHistory(position, amount, transactionDate, balanceBefore, balanceAfter);

        // Weryfikacja
        ArgumentCaptor<TransactionHistory> captor = ArgumentCaptor.forClass(TransactionHistory.class);
        verify(transactionHistoryRepository).save(captor.capture());

        TransactionHistory savedTransaction = captor.getValue();
        assertEquals(user, savedTransaction.getUser());
        assertEquals(position, savedTransaction.getPosition());
        assertEquals(amount, savedTransaction.getAmount());
        assertEquals(transactionDate, savedTransaction.getTransactionDate());
        assertEquals(balanceBefore, savedTransaction.getBalanceBeforeTransaction());
        assertEquals(balanceAfter, savedTransaction.getBalanceAfterTransaction());
    }
}
