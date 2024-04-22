package com.mintyfinance.web;

import com.mintyfinance.config.notification.NotificationService;
import com.mintyfinance.domain.exception.*;
import org.springframework.security.authentication.LockedException;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@ControllerAdvice
class ErrorController {
    private final NotificationService notificationService;

    public ErrorController(NotificationService notificationService) {
        this.notificationService = notificationService;
    }

    @ExceptionHandler(BalanceLimitExceededException.class)
    public String handleBalanceLimitExceededException(BalanceLimitExceededException ex, Model model) {
        model.addAttribute("error", ex.getMessage());
        return "error/404";
    }

    @ExceptionHandler(NoTransactionDatesException.class)
    public String handleNoTransactionDatesException(NoTransactionDatesException ex, Model model) {
        model.addAttribute("error", ex.getMessage());
        return "error/404";
    }

    @ExceptionHandler(ForbiddenAccessException.class)
    public String handleForbiddenAccessException(ForbiddenAccessException ex, Model model) {
        model.addAttribute("error", ex.getMessage());
        return "error/404";
    }

    @ExceptionHandler(TokenNotFoundException.class)
    public String handleTokenNotFoundException(TokenNotFoundException ex, Model model) {
        model.addAttribute("error", ex.getMessage());
        return "error/404";
    }

    @ExceptionHandler(UserNotFoundForPasswordResetException.class)
    public String handleUserNotFoundForPasswordResetException(UserNotFoundForPasswordResetException ex, Model model) {
        model.addAttribute("error", ex.getMessage());
        return "error/404";
    }

    @ExceptionHandler(UserNotFoundException.class)
    public String handleUserNotFoundException(UserNotFoundException ex, Model model) {
        model.addAttribute("error", ex.getMessage());
        return "error/404";
    }

    @ExceptionHandler(CategoryNotFoundException.class)
    public String handleCategoryNotFoundException(CategoryNotFoundException ex, Model model) {
        model.addAttribute("error", ex.getMessage());
        return "error/404";
    }

    @ExceptionHandler(GoalNotFoundException.class)
    public String handleGoalNotFoundException(GoalNotFoundException ex, Model model) {
        model.addAttribute("error", ex.getMessage());
        return "error/404";
    }

    @ExceptionHandler(PositionNotFoundException.class)
    public String handlePositionNotFoundException(PositionNotFoundException ex, Model model) {
        model.addAttribute("error", ex.getMessage());
        return "error/404";
    }

    @ExceptionHandler(UserRoleNotFoundException.class)
    public String UserRoleNotFoundException(UserRoleNotFoundException ex, Model model) {
        model.addAttribute("error", ex.getMessage());
        return "error/404";
    }

    @ExceptionHandler(CantDeleteCategoryWithPositions.class)
    public String handleCantDeleteCategoryWithPositions(CantDeleteCategoryWithPositions ex,
                                                        RedirectAttributes redirectAttributes) {
        notificationService.add(redirectAttributes, ex.getMessage());
        return "redirect:/category/categories";
    }
}
