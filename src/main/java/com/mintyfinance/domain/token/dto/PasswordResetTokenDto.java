package com.mintyfinance.domain.token.dto;

import com.mintyfinance.domain.user.User;

import java.time.LocalDateTime;

public class PasswordResetTokenDto {
    private Long tokenId;
    private String token;
    private User user;
    private LocalDateTime expiryDate;

    public PasswordResetTokenDto(Long tokenId, String token, User user, LocalDateTime expiryDate) {
        this.tokenId = tokenId;
        this.token = token;
        this.user = user;
        this.expiryDate = expiryDate;
    }

    public Long getTokenId() {
        return tokenId;
    }

    public void setTokenId(Long tokenId) {
        this.tokenId = tokenId;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public LocalDateTime getExpiryDate() {
        return expiryDate;
    }

    public void setExpiryDate(LocalDateTime expiryDate) {
        this.expiryDate = expiryDate;
    }
}
