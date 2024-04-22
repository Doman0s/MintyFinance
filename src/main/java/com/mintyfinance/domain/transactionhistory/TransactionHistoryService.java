package com.mintyfinance.domain.transactionhistory;

import com.mintyfinance.domain.exception.NoTransactionDatesException;
import com.mintyfinance.domain.position.Position;
import com.mintyfinance.domain.position.PositionService;
import com.mintyfinance.domain.transactionhistory.dto.TransactionHistoryDto;
import com.mintyfinance.domain.user.User;
import com.mintyfinance.domain.user.UserService;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class TransactionHistoryService {
    private final TransactionHistoryRepository transactionHistoryRepository;

    public TransactionHistoryService(TransactionHistoryRepository transactionHistoryRepository) {
        this.transactionHistoryRepository = transactionHistoryRepository;
    }

    public List<TransactionHistoryDto> findAllByUser(Long id) {
        return transactionHistoryRepository.findAllByUser_UserId(id)
                .stream().map(TransactionHistoryDtoMapper::map).toList();
    }

    public Map<String, MonthlySummary> createSummary(Long id) {
        // Tworzenie podsumowania. Na początek znajdujemy wszystkie wpisy danego użytkownika.
        List<TransactionHistoryDto> allUserHistoryEntries = findAllByUser(id);

        // Teraz tworzymy listę wpisów z historii posortowanych według daty
        List<LocalDateTime> sortedTransactionDates = transactionHistoryRepository.findAllTransactionDateByUser_UserIdOrderByTransactionDateAsc(id);
        if(sortedTransactionDates.isEmpty()) {
            throw new NoTransactionDatesException("Nie znaleziono żadnych dat transakcji dla użytkownika o id " + id);
        }

        // Teraz ustalamy zakres dat w podsumowaniu, szukamy najwcześniejszej daty i najpóźniejszej daty w tabeli
        LocalDateTime earliestTransactionDate = sortedTransactionDates.get(0);
        LocalDateTime latestTransactionDate = sortedTransactionDates.get(sortedTransactionDates.size() - 1);

        // Tworzymy mapę typu: miesiącDanegoRoku -> obiektMonthlySummary
        Map<String, MonthlySummary> summaryMap = new LinkedHashMap<>();

        // Zaczynamy liczenie od pierwszego dnia najwcześniejszego znalezionego miesiąca
        LocalDateTime currentMonthStart = earliestTransactionDate.withDayOfMonth(1).withHour(0).withMinute(0).withSecond(0).withNano(0);

        while (!currentMonthStart.isAfter(latestTransactionDate)) {
            LocalDateTime finalCurrentMonthStart = currentMonthStart;

            // Filtrujemy wpisy dla obecnego miesiąca
            List<TransactionHistoryDto> transactionsForCurrentMonth = allUserHistoryEntries.stream()
                    .filter(entry ->
                            !entry.getTransactionDate().isBefore(finalCurrentMonthStart) &&
                                    entry.getTransactionDate().isBefore(finalCurrentMonthStart.plusMonths(1)))
                    .collect(Collectors.toList());

            // Jeśli są jakiekolwiek transakcje w tym miesiącu
            if (!transactionsForCurrentMonth.isEmpty()) {
                // obliczamy łączne przychody jak i wydatki
                MonthlyIncomeExpense monthlyIncomeExpense = calculateIncomeAndExpense(transactionsForCurrentMonth);
                // tworzymy listę wszystkich transakcji z danego miesiąca
                MonthlySummary monthlySummary = new MonthlySummary(transactionsForCurrentMonth, monthlyIncomeExpense);

                String monthKeyForCurrent = currentMonthStart.getYear() + "-" + String.format("%02d", currentMonthStart.getMonthValue());
                summaryMap.put(monthKeyForCurrent, monthlySummary);
            }
            // Przesunięcie na następny miesiąc, ważny szczegół, modyfikujemy zmienną currentMonthStart, która jest
            // zadeklarowana przed pętlą while, więc do zmiennej finalCurr... w pętli przypisujemy kolejne miesiace.
            currentMonthStart = currentMonthStart.plusMonths(1);
        }

        return summaryMap;
    }

    public MonthlyIncomeExpense calculateIncomeAndExpense(List<TransactionHistoryDto> transactions) {
        BigDecimal totalIncome = transactions.stream()
                .map(TransactionHistoryDto::getAmount)
                .filter(amount -> amount.compareTo(BigDecimal.ZERO) > 0)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        BigDecimal totalExpense = transactions.stream()
                .map(TransactionHistoryDto::getAmount)
                .filter(amount -> amount.compareTo(BigDecimal.ZERO) < 0)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        return new MonthlyIncomeExpense(totalIncome, totalExpense);
    }

    public List<TransactionHistoryDto> findFilteredAndSortedByUser(Long id, String sortBy,
                                                                   String direction, Boolean isIncome) {
        Sort sort;
        if (sortBy != null && direction != null) {
            sort = Sort.by(direction.equalsIgnoreCase("asc") ? Sort.Order.asc(sortBy) : Sort.Order.desc(sortBy));
        } else {
            sort = Sort.unsorted();
        }

        List<TransactionHistory> entries;
        if (isIncome != null) {
            entries = transactionHistoryRepository.findAllByUser_UserIdAndPosition_IsIncome(id, isIncome, sort);
        } else {
            entries = transactionHistoryRepository.findAllByUser_UserId(id, sort);
        }

        return entries.stream().map(TransactionHistoryDtoMapper::map).toList();
    }

    @Transactional
    public void deleteById(Long id) {
        transactionHistoryRepository.deleteByTransactionHistoryId(id);
    }

    @Transactional
    public void deleteAllByUser(User user) {
        transactionHistoryRepository.deleteAllByUser(user);
    }

    @Transactional
    public void deleteAllByPositionId(Long positionId) {
        transactionHistoryRepository.deleteAllByPosition_PositionId(positionId);
    }

    @Transactional
    public void addEntryToHistory(Position position, BigDecimal amount, LocalDateTime dateBefore,
                                  BigDecimal balanceBefore, BigDecimal balanceAfter) {
        TransactionHistory transactionHistory = new TransactionHistory();
        transactionHistory.setUser(position.getUser());
        transactionHistory.setPosition(position);
        transactionHistory.setAmount(amount);
        transactionHistory.setTransactionDate(dateBefore);
        transactionHistory.setBalanceBeforeTransaction(balanceBefore);
        transactionHistory.setBalanceAfterTransaction(balanceAfter);

        transactionHistoryRepository.save(transactionHistory);
    }
}
