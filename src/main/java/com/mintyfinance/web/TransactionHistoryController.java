package com.mintyfinance.web;

import com.mintyfinance.config.notification.NotificationService;
import com.mintyfinance.domain.exception.UserNotFoundException;
import com.mintyfinance.domain.transactionhistory.MonthlySummary;
import com.mintyfinance.domain.transactionhistory.TransactionHistoryService;
import com.mintyfinance.domain.transactionhistory.dto.TransactionHistoryDto;
import com.mintyfinance.domain.user.User;
import com.mintyfinance.domain.user.UserService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

@Controller
@RequestMapping("/history")
public class TransactionHistoryController {
    private final TransactionHistoryService transactionHistoryService;
    private final UserService userService;
    private final NotificationService notificationService;

    public TransactionHistoryController(TransactionHistoryService transactionHistoryService, UserService userService,
                                        NotificationService notificationService) {
        this.transactionHistoryService = transactionHistoryService;
        this.userService = userService;
        this.notificationService = notificationService;
    }

    @GetMapping
    public String history(@RequestParam(name = "sortBy", required = false) String sortBy,
                          @RequestParam(name = "direction", required = false) String direction,
                          @RequestParam(name = "isIncome", required = false) Boolean isIncome,
                          Model model) {
        Long currentUserId = userService.getCurrentUserId();
        List<TransactionHistoryDto> historyList = transactionHistoryService.findFilteredAndSortedByUser(currentUserId,
                sortBy, direction, isIncome);

        model.addAttribute("direction", direction == null ? "desc" : direction);
        model.addAttribute("zero", BigDecimal.ZERO);
        model.addAttribute("history", historyList);
        return "history/history";
    }

    @GetMapping("/summary")
    public String summary(Model model) {
        Long currentUserId = userService.getCurrentUserId();
        Map<String, MonthlySummary> summaryEntries = transactionHistoryService.createSummary(currentUserId);

        model.addAttribute("zero", BigDecimal.ZERO);
        model.addAttribute("summaryEntries", summaryEntries);
        return "history/summary";
    }

    @DeleteMapping("/delete/{id}")
    public String deleteHistoryEntry(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        transactionHistoryService.deleteById(id);

        notificationService.add(redirectAttributes, "Pomyślnie usunięto wpis z historii.");
        return "redirect:/history";
    }

    @DeleteMapping("/delete")
    public String deleteUserHistory(RedirectAttributes redirectAttributes) {
        User user = userService.findClassicUserById(userService.getCurrentUserId())
                .orElseThrow(() -> new UserNotFoundException("Użytkownik nie został znaleziony"));
        transactionHistoryService.deleteAllByUser(user);

        notificationService.add(redirectAttributes, "Pomyślnie usunięto historię.");
        return "redirect:/history";
    }
}
