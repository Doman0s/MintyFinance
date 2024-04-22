package com.mintyfinance.domain.transaction;

import com.mintyfinance.domain.position.PositionService;
import com.mintyfinance.domain.position.RecurrenceType;
import com.mintyfinance.domain.transactionhistory.TransactionHistoryService;
import com.mintyfinance.domain.user.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.time.LocalDateTime;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.mock;

class TransactionServiceTest {
    private TransactionService transactionService;

    @BeforeEach
    void setUp() {
        PositionService mockPositionService = mock(PositionService.class);
        UserService mockUserService = mock(UserService.class);
        TransactionHistoryService mockTransactionHistoryService = mock(TransactionHistoryService.class);

        transactionService = new TransactionService(mockPositionService, mockUserService, mockTransactionHistoryService);
    }

    // Parametryzowany test dla wszystkich wartości RecurrenceType
    @ParameterizedTest
    @MethodSource("provideRebillDatesForRecurrenceTypes")
    void testCalculateNextRebillDate(RecurrenceType recurrenceType, LocalDateTime currentRebillDate, LocalDateTime expectedDate) {
        LocalDateTime result = transactionService.calculateNextRebillDate(currentRebillDate, recurrenceType);
        assertEquals(expectedDate, result);
    }

    // Metoda dostarczająca zestaw danych do testu
    private static Stream<Arguments> provideRebillDatesForRecurrenceTypes() {
        LocalDateTime currentRebillDate = LocalDateTime.now();
        return Stream.of(
                Arguments.of(RecurrenceType.ONCE, currentRebillDate, TransactionService.EXCLUSION_DATE),
                Arguments.of(RecurrenceType.DAILY, currentRebillDate, currentRebillDate.plusDays(1)),
                Arguments.of(RecurrenceType.WEEKLY, currentRebillDate, currentRebillDate.plusWeeks(1)),
                Arguments.of(RecurrenceType.BIWEEKLY, currentRebillDate, currentRebillDate.plusWeeks(2)),
                Arguments.of(RecurrenceType.MONTHLY, currentRebillDate, currentRebillDate.plusMonths(1)),
                Arguments.of(RecurrenceType.BIMONTHLY, currentRebillDate, currentRebillDate.plusMonths(2)),
                Arguments.of(RecurrenceType.SEMI_ANNUALLY, currentRebillDate, currentRebillDate.plusMonths(6)),
                Arguments.of(RecurrenceType.ANNUALLY, currentRebillDate, currentRebillDate.plusYears(1))
        );
    }
}
