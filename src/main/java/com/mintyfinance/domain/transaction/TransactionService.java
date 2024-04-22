package com.mintyfinance.domain.transaction;

import com.mintyfinance.domain.position.Position;
import com.mintyfinance.domain.position.PositionService;
import com.mintyfinance.domain.position.RecurrenceType;
import com.mintyfinance.domain.position.dto.PositionSaveDto;
import com.mintyfinance.domain.transactionhistory.TransactionHistoryService;
import com.mintyfinance.domain.user.User;
import com.mintyfinance.domain.user.UserService;
import com.mintyfinance.web.HomeController;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

import static com.mintyfinance.domain.position.RecurrenceType.DAILY;
import static com.mintyfinance.domain.position.RecurrenceType.WEEKLY;

@Service
public class TransactionService {
    private final PositionService positionService;
    private final UserService userService;
    private final TransactionHistoryService transactionHistoryService;

    //Jeśli pozycja ma się wykonać tylko raz (recurrenceType = ONCE), to po jej wykonaniu ustawiam tę datę, aby nie
    //wykonywała się już ponownie
    public static final LocalDateTime EXCLUSION_DATE =
            LocalDateTime.of(9999, 12, 31, 23, 59);

    public TransactionService(PositionService positionService, UserService userService,
                              TransactionHistoryService transactionHistoryService) {
        this.positionService = positionService;
        this.userService = userService;
        this.transactionHistoryService = transactionHistoryService;
    }

    @Transactional
    public void processTransactions() {
        List<Position> positionsToProcess = positionService.findPositionsToProcess(LocalDateTime.now(), EXCLUSION_DATE);

        for(Position position : positionsToProcess) {
            BigDecimal balanceBefore = position.getUser().getBalance();
            BigDecimal amount = position.isIncome() ? position.getAmount() : position.getAmount().negate();
            BigDecimal balanceAfter = balanceBefore.add(amount);

            userService.updateBalance(position.getUser().getUserId(), amount);

            position.setRebillDate(calculateNextRebillDate(position.getRebillDate(), position.getRecurrenceType()));
            positionService.addPosition(position);

            transactionHistoryService
                    .addEntryToHistory(position, amount, LocalDateTime.now(), balanceBefore, balanceAfter);
        }
    }

    public LocalDateTime calculateNextRebillDate(LocalDateTime currentRebillDate, RecurrenceType recurrenceType) {
        return switch (recurrenceType) {
            case ONCE -> EXCLUSION_DATE;
            case DAILY -> currentRebillDate.plusDays(1);
            case WEEKLY -> currentRebillDate.plusWeeks(1);
            case BIWEEKLY -> currentRebillDate.plusWeeks(2);
            case MONTHLY -> currentRebillDate.plusMonths(1);
            case BIMONTHLY -> currentRebillDate.plusMonths(2);
            case SEMI_ANNUALLY -> currentRebillDate.plusMonths(6);
            case ANNUALLY -> currentRebillDate.plusYears(1);
        };
    }
}
