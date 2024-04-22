package com.mintyfinance.domain.token;

import com.mintyfinance.domain.exception.TokenNotFoundException;
import com.mintyfinance.domain.exception.UserNotFoundForPasswordResetException;
import com.mintyfinance.domain.mail.EmailService;
import com.mintyfinance.domain.user.User;
import com.mintyfinance.domain.user.UserService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
public class PasswordResetTokenService {
    private final PasswordResetTokenRepository passwordResetTokenRepository;
    private final UserService userService;
    private final EmailService emailService;

    public PasswordResetTokenService(PasswordResetTokenRepository passwordResetTokenRepository, UserService userService,
                                     EmailService emailService) {
        this.passwordResetTokenRepository = passwordResetTokenRepository;
        this.userService = userService;
        this.emailService = emailService;
    }

    public PasswordResetToken generateTokenForUser(User user) {
        PasswordResetToken token = passwordResetTokenRepository.findByUser(user);
        if (token == null) {
            token = new PasswordResetToken();
            token.setUser(user);
        }

        token.setToken(UUID.randomUUID().toString());
        token.setExpiryDate(LocalDateTime.now().plusHours(24));
        return passwordResetTokenRepository.save(token);
    }

    public boolean validateToken(String tokenValue) {
        Optional<PasswordResetToken> token = passwordResetTokenRepository.findByToken(tokenValue);
        return token.filter(
                passwordResetToken -> !passwordResetToken.getExpiryDate().isBefore(LocalDateTime.now())
        ).isPresent();
    }

    public void handlePasswordResetRequest(String email) {
        Optional<User> userOptional = userService.findUserByEmail(email);

        userOptional.ifPresent(user -> {
            PasswordResetToken token = generateTokenForUser(user);
            String resetLink = "http://localhost:8080/reset-password-confirm?token=" + token.getToken();
            emailService.sendPasswordResetEmail(user.getEmail(), resetLink);
        });
    }

    @Transactional
    public void updatePasswordUsingToken(String token, String newPassword) {
        PasswordResetToken passwordResetToken = findByToken(token);

        User user = passwordResetToken.getUser();
        if (user == null) {
            throw new UserNotFoundForPasswordResetException(
                    "Użytkownik powiązany z tokenem resetowania hasła nie istnieje."
            );
        }

        userService.updatePassword(user, newPassword);
        passwordResetTokenRepository.delete(passwordResetToken);
    }

    public void cleanupExpiredTokens() {
        List<PasswordResetToken> expiredTokens = passwordResetTokenRepository.findAllByExpiryDateBefore(LocalDateTime.now());
        passwordResetTokenRepository.deleteAll(expiredTokens);
    }

    public PasswordResetToken findByToken(String tokenValue) {
        return passwordResetTokenRepository.findByToken(tokenValue)
                .orElseThrow(() -> new TokenNotFoundException("Token resetowania hasła nie istnieje."));
    }
}
