package com.mintyfinance.domain.user.dto;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;

public class PasswordResetDto {
    @NotBlank(message = "{NotBlank.passwordResetDto.newPassword}")
    @Size(min = 6, max = 32, message = "{Size.passwordResetDto.newPassword}")
    private String newPassword;
    @NotBlank(message = "{NotBlank.passwordResetDto.confirmPassword}")
    @Size(min = 6, max = 32, message = "{Size.passwordResetDto.confirmPassword}")
    private String confirmPassword;

    public PasswordResetDto() {
    }

    public String getNewPassword() {
        return newPassword;
    }

    public void setNewPassword(String newPassword) {
        this.newPassword = newPassword;
    }

    public String getConfirmPassword() {
        return confirmPassword;
    }

    public void setConfirmPassword(String confirmPassword) {
        this.confirmPassword = confirmPassword;
    }
}
