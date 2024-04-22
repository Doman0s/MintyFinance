package com.mintyfinance.domain.transaction;

import com.mintyfinance.domain.token.PasswordResetTokenService;
import com.mintyfinance.domain.user.UserService;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

@Service
public class ScheduledTasks {
    private final TransactionService transactionService;
    private final PasswordResetTokenService passwordResetTokenService;

    public ScheduledTasks(TransactionService transactionService, PasswordResetTokenService passwordResetTokenService) {
        this.transactionService = transactionService;
        this.passwordResetTokenService = passwordResetTokenService;
    }

//    @Scheduled(fixedRate = 20000)  // Co 20 sekund
    @Scheduled(cron = "0 * * * * *")  // Co minutę
    public void processTransaction() {
        transactionService.processTransactions();
    }

    @Scheduled(cron = "0 0 0 */2 * *") // Co 2 dni o północy
    public void cleanupExpiredPasswordResetTokens() {
        passwordResetTokenService.cleanupExpiredTokens();
    }
}
