package com.mintyfinance.web;

import com.mintyfinance.config.notification.NotificationService;
import com.mintyfinance.domain.exception.ForbiddenAccessException;
import com.mintyfinance.domain.exception.GoalNotFoundException;
import com.mintyfinance.domain.exception.UserNotFoundException;
import com.mintyfinance.domain.goal.Goal;
import com.mintyfinance.domain.goal.GoalService;
import com.mintyfinance.domain.goal.dto.GoalDto;
import com.mintyfinance.domain.user.User;
import com.mintyfinance.domain.user.UserService;
import com.mintyfinance.domain.user.dto.UserDto;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.validation.Valid;
import java.util.List;

@Controller
@RequestMapping("/goal")
public class GoalController {
    private final GoalService goalService;
    private final UserService userService;
    private final NotificationService notificationService;

    public GoalController(GoalService goalService, UserService userService, NotificationService notificationService) {
        this.goalService = goalService;
        this.userService = userService;
        this.notificationService = notificationService;
    }

    @GetMapping("/goals")
    public String getGoalList(Model model) {
        List<GoalDto> goals = goalService.findAllByUser_UserId(userService.getCurrentUserId());

        model.addAttribute("goals", goals);
        return "goal/goal-listing";
    }

    @GetMapping("/add")
    public String addGoalForm(Model model) {
        model.addAttribute("goal", new GoalDto());
        return "goal/goal-form";
    }

    @PostMapping("/add")
    public String addGoal(@Valid @ModelAttribute("goal") GoalDto goal, BindingResult result,
                          RedirectAttributes redirectAttributes, Model model) {
        if (result.hasErrors()) {
            model.addAttribute("goal", goal);
            return "goal/goal-form";
        }
        goalService.addGoal(goal);

        notificationService.add(redirectAttributes, "Cel %s został zapisany.", goal.getName());
        return "redirect:/goal/goals";
    }

    @GetMapping("/edit/{id}")
    public String editGoalForm(@PathVariable Long id, Model model) {
        goalService.validateUserOwnershipOfGoal(id);
        GoalDto goal = goalService.findGoalById(id)
                .orElseThrow(() -> new GoalNotFoundException("Nie znaleziono celu o id " + id));

        model.addAttribute("goal", goal);
        return "goal/edit-goal";
    }

    @PostMapping("/edit")
    public String udpateGoal(@Valid @ModelAttribute("goal") Goal goal, BindingResult result,
                             RedirectAttributes redirectAttributes, Model model) {
        if (result.hasErrors()) {
            model.addAttribute("goal", goal);
            return "goal/edit-goal";
        }
        goalService.updateGoal(goal);

        notificationService.add(redirectAttributes, "Dane celu '%s' zostały zaktualizowane.", goal.getName());
        return "redirect:/goal/goals";
    }

    @GetMapping("/complete/{id}")
    public String getGoalList(@PathVariable Long id, Model model, RedirectAttributes redirectAttributes) {
        String message = goalService.completeGoal(id);
        List<GoalDto> goals = goalService.findAllByUser_UserId(userService.getCurrentUserId());

        model.addAttribute("goals", goals);
        notificationService.add(redirectAttributes, message);
        return "redirect:/goal/goals";
    }

    @DeleteMapping("/delete/{id}")
    public String deleteGoal(@PathVariable Long id, RedirectAttributes redirectAttributes) {
//        long startTime = System.nanoTime();

        goalService.deleteBy_GoalId(id);

//        long endTime = System.nanoTime();
//        long duration = (endTime - startTime) / 1_000_000;
//        System.out.println("Usuwanie celu (w kontrolerze) trwało: " + duration + " ms");

        notificationService.add(redirectAttributes, "Cel został pomyślnie usunięty.");
        return "redirect:/goal/goals";
    }

    @DeleteMapping("/delete")
    public String deleteGoals(RedirectAttributes redirectAttributes) {
        User user = userService.findClassicUserById(userService.getCurrentUserId())
                .orElseThrow(() -> new UserNotFoundException(
                        "Nie znaleziono użytkownika o id " + userService.getCurrentUserId())
                );
        goalService.deleteAllByUser(user);

        notificationService.add(redirectAttributes, "Cele zostały usunięte.");
        return "redirect:/goal/goals";
    }
}
