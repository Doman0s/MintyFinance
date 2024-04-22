package com.mintyfinance.domain.position;

import com.mintyfinance.domain.category.Category;
import com.mintyfinance.domain.user.User;
import org.springframework.data.domain.Sort;
import org.springframework.data.repository.CrudRepository;

import java.time.LocalDateTime;
import java.util.List;

public interface PositionRepository extends CrudRepository<Position, Long> {
    List<Position> findAllByCategory_NameIgnoreCase(String category);
    List<Position> findAllByUser_UserId(Long id);
    List<Position> findAllByUser_UserIdAndIsIncome(Long id, Boolean isIncome);
    List<Position> findAllByUser_UserIdAndRecurrenceType(long userId, RecurrenceType recurrenceType);
    List<Position> findAllByUser_UserIdAndIsIncomeAndRecurrenceType(long userId, Boolean isIncome, RecurrenceType recurrenceType);
    List<Position> findAllByUser_UserIdAndCategory_CategoryId(Long userId, Long categoryId);
    List<Position> findAllByUser_UserIdAndNameContainingIgnoreCase(Long userId, String name);
    List<Position> findByRebillDateLessThanEqualAndRebillDateNot(LocalDateTime targetDate, LocalDateTime exclusionDate);
    List<Position> findAllByCategory_CategoryId(Long categoryId);
    void deleteById(Long id);
    void deleteAllByUser(User user);
    void deleteAllByCategory(Category category);
}
