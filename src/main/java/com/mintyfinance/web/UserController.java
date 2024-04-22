package com.mintyfinance.web;

import com.mintyfinance.config.notification.NotificationService;
import com.mintyfinance.domain.exception.ForbiddenAccessException;
import com.mintyfinance.domain.exception.UserNotFoundException;
import com.mintyfinance.domain.mail.EmailService;
import com.mintyfinance.domain.position.dto.PositionDto;
import com.mintyfinance.domain.token.PasswordResetToken;
import com.mintyfinance.domain.token.PasswordResetTokenService;
import com.mintyfinance.domain.user.User;
import com.mintyfinance.domain.user.UserRepository;
import com.mintyfinance.domain.user.UserService;
import com.mintyfinance.domain.user.dto.PasswordChangeDto;
import com.mintyfinance.domain.user.dto.UserCredentialsDto;
import com.mintyfinance.domain.user.dto.UserDto;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.authentication.logout.SecurityContextLogoutHandler;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import javax.validation.Valid;
import java.util.Optional;

@Controller
@RequestMapping("/user")
public class UserController {
    private final UserService userService;
    private final PasswordEncoder passwordEncoder;
    private final NotificationService notificationService;

    public UserController(UserService userService, PasswordEncoder passwordEncoder,
                          NotificationService notificationService) {
        this.userService = userService;
        this.passwordEncoder = passwordEncoder;
        this.notificationService = notificationService;
    }

    @GetMapping("/account")
    public String getUserPanel() {
        return "user/account";
    }

    @GetMapping("/finance-management")
    public String getFinanceManagementPanel() {
        return "user/finance-management";
    }

    @GetMapping("/profile/{id}")
    public String userProfile(@PathVariable Long id, Model model) {
        UserDto user = userService.validateUserOwnershipOfProfileAndReturnUser(id);
        String genderString = String.valueOf(user.getGender());

        model.addAttribute("user", user);
        model.addAttribute("gender", genderString);
        return "user/profile";
    }

    @GetMapping("/edit/{id}")
    public String editUserForm(@PathVariable Long id, Model model) {
        UserDto user = userService.validateUserOwnershipOfProfileAndReturnUser(id);

        model.addAttribute("oldEmail", user.getEmail());
        model.addAttribute("user", user);
        return "user/edit";
    }

    @PostMapping("/edit")
    public String updateUser(@Valid @ModelAttribute("user") UserDto user, BindingResult result,
                             @RequestParam String oldEmail, HttpServletRequest request, HttpServletResponse response,
                             RedirectAttributes redirectAttributes, Model model) {
        if (result.hasErrors()) {
            model.addAttribute("user", user);
            return "user/edit";
        }

        if (!oldEmail.equals(user.getEmail()) && userService.checkIfEmailAlreadyExists(user.getEmail())) {
            model.addAttribute("errorMessage", "Podany adres email jest już zajęty.");
            return "user/edit";
        }
        userService.updateUser(user);

        if (!oldEmail.equals(user.getEmail())) {
            new SecurityContextLogoutHandler().logout(
                    request, response, SecurityContextHolder.getContext().getAuthentication()
            );
            notificationService.add(redirectAttributes,
                    "Twoje dane zostały zaktualizowane, a ponieważ zmieniłeś adres email, musisz zalogować się "
                    + "ponownie.");
            return "redirect:/login?logout";
        }

        notificationService.add(redirectAttributes, "Twoje dane zostały zaktualizowane.");
        return "redirect:/user/profile/" + user.getUserId();
    }

    @GetMapping("/change-password/{id}")
    public String editUserPasswordForm(@PathVariable Long id, Model model) {
        userService.validateUserOwnershipOfProfile(id);

        model.addAttribute("passwordChange", new PasswordChangeDto());
        model.addAttribute("userID", id);
        return "user/change-password";
    }

    @PostMapping("/change-password")
    public String changeUserPassword(@Valid @ModelAttribute("passwordChange") PasswordChangeDto passwordChange,
                                     BindingResult result, RedirectAttributes redirectAttributes, Model model) {
        if (result.hasErrors()) {
            model.addAttribute("passwordChange", passwordChange);
            return "user/change-password";
        }

        User user = userService.findClassicUserById(userService.getCurrentUserId())
                .orElseThrow(() -> new UserNotFoundException(
                        "Nie znaleziono użytkownika o id " + userService.getCurrentUserId()
                ));

        if (!passwordEncoder.matches(passwordChange.getOldPassword(), user.getPassword())) {
            notificationService.add(redirectAttributes, "Stare hasło jest nieprawidłowe.");
            return "redirect:/user/change-password/" + user.getUserId();
        }

        if (!passwordChange.getNewPassword().equals(passwordChange.getConfirmPassword())) {
            notificationService.add(redirectAttributes, "Nowe hasła nie są takie same.");
            return "redirect:/user/change-password/" + user.getUserId();
        }

        userService.updatePassword(user, passwordChange.getNewPassword());
        notificationService.add(redirectAttributes, "Hasło zostało zmienione.");
        return "redirect:/";
    }

    @PostMapping("/close-account")
    public String closeAccount(RedirectAttributes redirectAttributes, HttpServletRequest request,
                               HttpServletResponse response) {
        User user = userService.findClassicUserById(userService.getCurrentUserId())
                .orElseThrow(() -> new UserNotFoundException(
                        "Nie znaleziono użytkownika o id " + userService.getCurrentUserId()
                ));

        userService.closeAccount(user);
        // Zakończ sesję użytkownika i wyloguj
        new SecurityContextLogoutHandler().logout(
                request, response, SecurityContextHolder.getContext().getAuthentication()
        );

        notificationService.add(redirectAttributes, "Konto zostało zamknięte.");
        return "redirect:/login?logout";
    }
}

