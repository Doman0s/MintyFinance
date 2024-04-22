package com.mintyfinance.domain.token;

import com.mintyfinance.domain.user.User;
import org.springframework.data.repository.CrudRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface PasswordResetTokenRepository extends CrudRepository<PasswordResetToken, Long> {
    Optional<PasswordResetToken> findByToken(String tokenValue);
    PasswordResetToken findByUser(User user);
    List<PasswordResetToken> findAllByExpiryDateBefore(LocalDateTime value);
    void deleteAllByUser(User user);
}
