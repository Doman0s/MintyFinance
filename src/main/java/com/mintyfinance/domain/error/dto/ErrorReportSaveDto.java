package com.mintyfinance.domain.error.dto;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;

public class ErrorReportSaveDto {
    private Long user;
    @NotBlank(message = "{NotBlank.errorReportSaveDto.description}")
    @Size(max = 500, message = "{Size.errorReportSaveDto.description}")
    private String description;

    public Long getUser() {
        return user;
    }

    public void setUser(Long user) {
        this.user = user;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }
}
