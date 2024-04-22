package com.mintyfinance.domain.goal;

import com.mintyfinance.domain.category.Category;
import com.mintyfinance.domain.category.dto.CategoryDto;
import com.mintyfinance.domain.exception.ForbiddenAccessException;
import com.mintyfinance.domain.exception.GoalNotFoundException;
import com.mintyfinance.domain.exception.UserNotFoundException;
import com.mintyfinance.domain.goal.dto.GoalDto;
import com.mintyfinance.domain.user.User;
import com.mintyfinance.domain.user.UserRepository;
import com.mintyfinance.domain.user.UserService;
import com.mintyfinance.domain.user.dto.UserCredentialsDto;
import com.mintyfinance.web.HomeController;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

@Service
public class GoalService {
    private final GoalRepository goalRepository;
    private final UserService userService;
    private final UserRepository userRepository;

    public GoalService(GoalRepository goalRepository, UserService userService, UserRepository userRepository) {
        this.goalRepository = goalRepository;
        this.userService = userService;
        this.userRepository = userRepository;
    }

    public void validateUserOwnershipOfGoal(Long goalId) {
        GoalDto goal = findGoalById(goalId)
                .orElseThrow(() -> new GoalNotFoundException("Nie znaleziono celu o id " + goalId));

        Long userId = userService.getCurrentUserId();
        if (!goal.getUser().getUserId().equals(userId)) {
            throw new ForbiddenAccessException("Brak dostępu");
        }
    }

    public List<GoalDto> findAllByUser_UserId(Long userId) {
        List<Goal> goals = goalRepository.findAllByUser_UserIdOrderByIsCompletedAsc(userId);
        return goals.stream().map(GoalDtoMapper::map).toList();
    }

    public Optional<GoalDto> findGoalById(Long id) {
        return goalRepository.findById(id).map(GoalDtoMapper::map);
    }

    @Transactional
    public void deleteAllByUser(User user) {
        goalRepository.deleteAllByUser(user);
    }

    @Transactional
    public void deleteBy_GoalId(Long id) {
        goalRepository.deleteByGoalId(id);
    }

    @Transactional
    public void addGoal(GoalDto goalDto) {
        Goal goal = new Goal();
        User user = userRepository.findById(userService.getCurrentUserId())
                .orElseThrow(() -> new UserNotFoundException(
                        "Nie znaleziono użytkownika o id " + userService.getCurrentUserId())
                );
        goal.setUser(user);
        goal.setName(goalDto.getName());
        goal.setAmount(goalDto.getAmount());
        goal.setCompleted(false);
        goalRepository.save(goal);
    }

    @Transactional
    public void updateGoal(Goal updateGoal) {
        Goal goal = goalRepository.findById(updateGoal.getGoalId())
                .orElseThrow(() -> new GoalNotFoundException("Nie znaleziono celu o id " + updateGoal.getGoalId()));
        goal.setName(updateGoal.getName());
        goal.setUser(userService.findClassicUserById(userService.getCurrentUserId())
                .orElseThrow(() -> new UserNotFoundException(
                        "Nie znaleziono użytkownika o id " + userService.getCurrentUserId())
                ));
        goal.setAmount(updateGoal.getAmount());
        goal.setCompleted(updateGoal.isCompleted());
        goalRepository.save(goal);
    }

    @Transactional
    public String completeGoal(Long id) {
        Goal goal = goalRepository.findById(id)
                .orElseThrow(() -> new GoalNotFoundException("Nie znaleziono celu o id " + id));
        BigDecimal balance = userService.getCurrentUserBalance();

        if (balance.compareTo(goal.getAmount()) < 0) {
            return "Nie można ukończyc celu, brak wystarczającej ilości środków na koncie.";
        }

        goal.setCompleted(true);
        goalRepository.save(goal);
        userService.decreaseBalance(goal.getAmount());
        return "Cel został zakończony, pobrano pieniądze z konta.";
    }
}
