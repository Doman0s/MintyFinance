package com.mintyfinance.domain.transactionhistory;

import com.mintyfinance.domain.position.Position;
import com.mintyfinance.domain.user.User;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface TransactionHistoryRepository extends JpaRepository<TransactionHistory, Long> {
    List<TransactionHistory> findAllByUser_UserIdAndPosition_IsIncome(Long id, Boolean isIncome, Sort sort);
    List<TransactionHistory> findAllByUser_UserId(Long id, Sort sort);
    List<TransactionHistory> findAllByUser_UserId(Long id);
    @Query("SELECT t.transactionDate FROM TransactionHistory t WHERE t.user.userId = :id ORDER BY t.transactionDate ASC")
    List<LocalDateTime> findAllTransactionDateByUser_UserIdOrderByTransactionDateAsc(@Param("id") Long userId);
    void deleteByTransactionHistoryId(Long id);
    void deleteAllByPosition_PositionId(Long positionId);
    void deleteAllByUser(User user);
}
