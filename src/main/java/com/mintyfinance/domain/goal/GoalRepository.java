package com.mintyfinance.domain.goal;

import com.mintyfinance.domain.category.Category;
import com.mintyfinance.domain.user.User;
import org.springframework.data.repository.CrudRepository;

import java.util.List;

public interface GoalRepository extends CrudRepository<Goal, Long> {
    List<Goal> findAllByUser_UserIdOrderByIsCompletedAsc(Long userId);
    void deleteAllByUser(User user);
    void deleteByGoalId(Long id);
}
