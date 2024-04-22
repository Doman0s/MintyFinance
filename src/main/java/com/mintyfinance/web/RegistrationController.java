package com.mintyfinance.web;

import com.mintyfinance.config.notification.NotificationService;
import com.mintyfinance.domain.user.UserService;
import com.mintyfinance.domain.user.dto.UserRegistrationDto;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.validation.Valid;
import java.math.BigDecimal;

@Controller
public class RegistrationController {
    private final UserService userService;
    private final NotificationService notificationService;

    public RegistrationController(UserService userService, NotificationService notificationService) {
        this.userService = userService;
        this.notificationService = notificationService;
    }

    @GetMapping("/registration")
    public String registrationForm(Model model) {
        UserRegistrationDto user = new UserRegistrationDto();
        user.setBalance(BigDecimal.ZERO);

        model.addAttribute("user", user);
        return "home/registration-form";
    }

    @PostMapping("/registration")
    public String register(@Valid @ModelAttribute("user") UserRegistrationDto userRegistration, BindingResult result,
                           Model model, RedirectAttributes redirectAttributes) {
        if (result.hasErrors()) {
            model.addAttribute("user", userRegistration);
            return "home/registration-form";
        }

        if (userService.checkIfEmailAlreadyExists(userRegistration.getEmail())) {
            model.addAttribute("errorMessage", "Podany adres email jest zajęty.");
            return "/home/registration-form";
        }

        userService.registerUserWithDefaultRole(userRegistration);
        notificationService.add(redirectAttributes, "Stworzono konto. Możesz się teraz zalogować.");
        return "redirect:/login";
    }
}
