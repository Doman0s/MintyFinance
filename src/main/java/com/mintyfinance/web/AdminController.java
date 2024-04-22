package com.mintyfinance.web;

import com.mintyfinance.config.notification.NotificationService;
import com.mintyfinance.domain.blocked.BlockedService;
import com.mintyfinance.domain.blocked.dto.BlockedDto;
import com.mintyfinance.domain.error.ErrorReport;
import com.mintyfinance.domain.error.ErrorReportService;
import com.mintyfinance.domain.error.dto.ErrorReportDto;
import com.mintyfinance.domain.user.UserService;
import com.mintyfinance.domain.user.dto.UserCredentialsDto;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.*;

@Controller
@RequestMapping("/admin")
class AdminController {
    private final UserService userService;
    private final BlockedService blockedService;
    private final ErrorReportService errorReportService;
    private final NotificationService notificationService;

    public AdminController(UserService userService, BlockedService blockedService, ErrorReportService errorReportService,
                           NotificationService notificationService) {
        this.userService = userService;
        this.blockedService = blockedService;
        this.errorReportService = errorReportService;
        this.notificationService = notificationService;
    }

    @GetMapping
    public String getAdminPanel() {
        return "admin/admin";
    }

    @GetMapping("/user-listing")
    public String blockUsers(@RequestParam(name = "filter", defaultValue = "all") String filter, Model model) {
        List<UserCredentialsDto> users = userService.getUsersByFilter(filter);
        List<Long> bannedUsersIds = userService.getBannedUsersIds();

        model.addAttribute("users", users);
        model.addAttribute("bannedUsersIds", bannedUsersIds);
        model.addAttribute("heading", "Blokowanie użytkowników");
        model.addAttribute("description",
                "Poniżej znajdziesz listę wszystkich użytkowników w bazie danych");
        return "admin/user-listing";
    }

    @GetMapping("/search")
    public String searchUsers(@RequestParam(name = "email") String email, Model model) {
        Map<String, Object> searchResult = userService.searchUsers(email);

        model.addAttribute("users", searchResult.get("users"));
        model.addAttribute("bannedUsersIds", searchResult.get("bannedUsersIds"));
        model.addAttribute("heading", "Wyniki wyszukiwania dla adresu '" + email + "'");
        model.addAttribute("description", "Użytkownicy zgodni z kryteriami wyszukiwania");
        return "admin/user-listing";
    }

    @GetMapping("/block/{id}")
    public String blockUser(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        BlockedDto bannedUser = new BlockedDto();
        bannedUser.setUserId(id);
        blockedService.saveBlocked(bannedUser);

        notificationService.add(redirectAttributes, "Pomyślnie zablokowano użytkownika.");
        return "redirect:/admin/user-listing";
    }

    @GetMapping("/unblock/{id}")
    public String unblockUser(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        blockedService.deleteBlocked(id);

        notificationService.add(redirectAttributes, "Pomyślnie odblokowano użytkownika.");
        return "redirect:/admin/user-listing";
    }

    @GetMapping("/error-listing")
    public String getErrors(Model model) {
        List<ErrorReportDto> allErrors = errorReportService.findAllErrors();

        model.addAttribute("showUserEmail", true);
        model.addAttribute("heading", "Zgłoszone błędy");
        model.addAttribute("description",
                "Poniżej znajdziesz listę wszystkich błędów zgłoszonych przez użytkowników.");
        model.addAttribute("errors", allErrors);
        return "admin/error-listing";
    }

    @GetMapping("/search-user-errors")
    public String searchErrors(@RequestParam(name = "email") String email, Model model) {
        List<ErrorReport> errors = errorReportService.findErrorsByEmail(email);

        model.addAttribute("showUserEmail", false);
        model.addAttribute("heading", "Wyniki wyszukiwania dla adresu '" + email + "'");
        model.addAttribute("description", "Użytkownicy zgodni z kryteriami wyszukiwania");
        model.addAttribute("errors", errors);
        model.addAttribute("email", email);
        return "admin/error-listing";
    }

    @DeleteMapping("/error-report/delete/{id}")
    public String deleteErrorReport(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        errorReportService.deleteErrorReport(id);

        notificationService.add(redirectAttributes, "Pomyślnie usunięto błąd.");
        return "redirect:/admin/error-listing";
    }
}