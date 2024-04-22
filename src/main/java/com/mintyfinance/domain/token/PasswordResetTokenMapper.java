package com.mintyfinance.domain.token;

import com.mintyfinance.domain.token.dto.PasswordResetTokenDto;

class PasswordResetTokenMapper {
    static PasswordResetTokenDto map(PasswordResetToken passwordResetToken) {
        return new PasswordResetTokenDto(
                passwordResetToken.getTokenId(),
                passwordResetToken.getToken(),
                passwordResetToken.getUser(),
                passwordResetToken.getExpiryDate()
        );
    }
}
