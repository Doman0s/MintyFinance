package com.mintyfinance.domain.position;

import com.mintyfinance.domain.category.Category;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.*;

import com.mintyfinance.domain.transaction.TransactionService;
import com.mintyfinance.domain.user.User;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

@ExtendWith(MockitoExtension.class)
class PositionServiceTest {
    @InjectMocks
    private PositionService positionService;

    @Mock
    private PositionRepository positionRepository;

    @Test
    public void testFindPositionsToProcess_MultiplePositions() {
        LocalDateTime now = LocalDateTime.now();
        List<Position> allPositions = createExamplePositions(now);
        List<Position> expectedPositions = Arrays.asList(allPositions.get(0), allPositions.get(1));

        when(positionRepository.findByRebillDateLessThanEqualAndRebillDateNot(eq(now),
                eq(TransactionService.EXCLUSION_DATE))).thenReturn(expectedPositions);
        List<Position> actualPositions = positionService.findPositionsToProcess(now, TransactionService.EXCLUSION_DATE);

        assertEquals(expectedPositions, actualPositions);
    }

    @Test
    public void testFindPositionsToProcess_NoPositions() {
        LocalDateTime now = LocalDateTime.now();
        when(positionRepository.findByRebillDateLessThanEqualAndRebillDateNot(eq(now),
                eq(TransactionService.EXCLUSION_DATE))).thenReturn(Collections.emptyList());

        List<Position> actualPositions = positionService.findPositionsToProcess(now, TransactionService.EXCLUSION_DATE);

        assertTrue(actualPositions.isEmpty());
    }


    private static List<Position> createExamplePositions(LocalDateTime currentDateTime) {
        LocalDateTime dateTime1 = LocalDateTime.of(2023, 9, 15, 20, 0);
        LocalDateTime dateTime2 = LocalDateTime.of(2023, 10, 2, 8, 0);
        LocalDateTime dateTime3 = LocalDateTime.of(2024, 4, 1, 20, 0);
        LocalDateTime dateTime4 = TransactionService.EXCLUSION_DATE;

        return Arrays.asList(
            new Position(1L, new Category(), new User(), "Spotify", "Opis spotify",
                    false, (byte) 2, BigDecimal.valueOf(25), dateTime1, RecurrenceType.MONTHLY),
            new Position(2L, new Category(), new User(), "Wynagrodzenie", "Opis wynagrodzenie",
                    true, (byte) 10, BigDecimal.valueOf(5000), dateTime2, RecurrenceType.MONTHLY),
            new Position(3L, new Category(), new User(), "Rachunek za prąd", "Opis rachunek",
                    false, (byte) 9, BigDecimal.valueOf(3500), dateTime3, RecurrenceType.MONTHLY),
            new Position(4L, new Category(), new User(), "Prezent", "Opis prezent",
                false, (byte) 7, BigDecimal.valueOf(200), dateTime4, RecurrenceType.ANNUALLY),
            new Position(5L, new Category(), new User(), "Kieszonkowe", "Opis kieszonkowe",
                true, (byte) 6, BigDecimal.valueOf(100), currentDateTime, RecurrenceType.BIWEEKLY)
        );
    }
}
