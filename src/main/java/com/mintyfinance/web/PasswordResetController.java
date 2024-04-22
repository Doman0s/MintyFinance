package com.mintyfinance.web;

import com.mintyfinance.config.notification.NotificationService;
import com.mintyfinance.domain.token.PasswordResetTokenService;
import com.mintyfinance.domain.user.dto.PasswordResetDto;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.validation.Valid;

@Controller
public class PasswordResetController {
    private final PasswordResetTokenService passwordResetTokenService;
    private final NotificationService notificationService;

    public PasswordResetController(PasswordResetTokenService passwordResetTokenService,
                                   NotificationService notificationService) {
        this.passwordResetTokenService = passwordResetTokenService;
        this.notificationService = notificationService;
    }

    @GetMapping("/reset-password")
    public String resetPassword() {
        return "home/reset-password-form";
    }

    @PostMapping("/reset-password")
    public String handlePasswordResetRequest(@RequestParam String email, RedirectAttributes redirectAttributes) {
        passwordResetTokenService.handlePasswordResetRequest(email);

        notificationService.add(redirectAttributes, "Jeśli podany adres e-mail jest zarejestrowany w aplikacji"
                + "została na niego wysłana wiadomość resetowania hasła.");
        return "redirect:/login";
    }

    @GetMapping("/reset-password-confirm")
    public String showPasswordResetForm(@RequestParam String token, Model model,
                                        RedirectAttributes redirectAttributes) {
        boolean isTokenValid = passwordResetTokenService.validateToken(token);
        if (!isTokenValid) {
            notificationService.add(redirectAttributes, "Nieprawidłowy adres. Spróbuj ponownie.");
            return "redirect:/login";
        }
        model.addAttribute("passwordResetDto", new PasswordResetDto());
        model.addAttribute("token", token);
        return "home/reset-password-confirm";
    }

    @PostMapping("/reset-password-confirm")
    public String handlePasswordResetConfirmation(@RequestParam String token, @Valid PasswordResetDto passwordResetDto,
                                                  BindingResult result, Model model,
                                                  RedirectAttributes redirectAttributes) {
        if (result.hasErrors()) {
            model.addAttribute("passwordResetDto", passwordResetDto);
            model.addAttribute("token", token);
            return "home/reset-password-confirm";
        }

        boolean arePasswordsEqual = passwordResetDto.getNewPassword().equals(passwordResetDto.getConfirmPassword());
        if (!arePasswordsEqual) {
            model.addAttribute("passwordResetDto", passwordResetDto);
            notificationService.add(redirectAttributes, "Hasła nie są takie same!");
            return "redirect:/reset-password-confirm?token=" + token;
        }

        passwordResetTokenService.updatePasswordUsingToken(token, passwordResetDto.getNewPassword());
        notificationService.add(redirectAttributes, "Hasło zostało pomyślnie zmienione.");
        return "redirect:/login";
    }
}
