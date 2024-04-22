package com.mintyfinance.domain.user.dto;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;

public class PasswordChangeDto {
    @NotBlank(message = "{NotBlank.passwordChangeDto.oldPassword}")
    private String oldPassword;
    @NotBlank(message = "{NotBlank.passwordChangeDto.newPassword}")
    @Size(min = 6, max = 32, message = "{Size.passwordChangeDto.newPassword}")
    private String newPassword;
    @NotBlank(message = "{NotBlank.passwordChangeDto.confirmPassword}")
    @Size(min = 6, max = 32, message = "{Size.passwordChangeDto.confirmPassword}")
    private String confirmPassword;

    public PasswordChangeDto() {
    }

    public String getOldPassword() {
        return oldPassword;
    }

    public void setOldPassword(String oldPassword) {
        this.oldPassword = oldPassword;
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
