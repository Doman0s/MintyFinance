package com.mintyfinance.web;

import com.mintyfinance.config.notification.NotificationService;
import com.mintyfinance.domain.mail.EmailService;
import com.mintyfinance.domain.position.PositionService;
import com.mintyfinance.domain.position.RecurrenceType;
import com.mintyfinance.domain.position.dto.PositionDto;
import com.mintyfinance.domain.token.PasswordResetToken;
import com.mintyfinance.domain.token.PasswordResetTokenService;
import com.mintyfinance.domain.user.User;
import com.mintyfinance.domain.user.UserRepository;
import com.mintyfinance.domain.user.UserService;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;
import java.util.Optional;

@Controller
public class HomeController {
    private final PositionService positionService;
    private final UserService userService;

    public HomeController(PositionService positionService, UserService userService) {
        this.positionService = positionService;
        this.userService = userService;
    }

    @GetMapping("/")
    public String home(@RequestParam(name = "sortBy", required = false) String sortBy,
                       @RequestParam(name = "direction", required = false) String direction,
                       @RequestParam(name = "isIncome", required = false) Boolean isIncome,
                       @RequestParam(name = "recurrenceType", required = false) RecurrenceType recurrenceType,
                       Model model) {
        if (userService.isAuthenticated()) {
            List<PositionDto> positions = positionService.findFilteredAndSortedPositionsByUserId(
                    userService.getCurrentUserId(), sortBy, direction, isIncome, recurrenceType
            );
            model.addAttribute("heading", "Zdefiniowane pozycje");
            model.addAttribute("positions", positions);
            model.addAttribute("direction", direction == null ? "asc" : direction);
            return "position/position-listing";
        }
        return "home/mainDescription";
    }

    @GetMapping("/login")
    public String loginForm(Authentication authentication) {
        if (authentication != null && authentication.isAuthenticated()) {
            return "redirect:/";
        }
        return "home/login-form";
    }

    @GetMapping("/help-support")
    public String getHelpSupportPanel() {
        return "home/help-support";
    }

    @GetMapping("/about")
    public String about() {
        return "home/about";
    }

    @GetMapping("/contact")
    public String contact() {
        return "home/contact";
    }
}
