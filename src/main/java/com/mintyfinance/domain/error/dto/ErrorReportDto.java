package com.mintyfinance.domain.error.dto;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class ErrorReportDto {
    private Long errorReportId;
    private String user;
    private LocalDateTime reportDate;
    @NotBlank @Size(max = 500)
    private String description;

    public ErrorReportDto(Long errorReportId, String user, LocalDateTime reportDate, String description) {
        this.errorReportId = errorReportId;
        this.user = user;
        this.reportDate = reportDate;
        this.description = description;
    }

    public Long getErrorReportId() {
        return errorReportId;
    }

    public void setErrorReportId(Long errorReportId) {
        this.errorReportId = errorReportId;
    }

    public String getUser() {
        return user;
    }

    public void setUser(String user) {
        this.user = user;
    }

    public String getFormattedReportDate() {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm");
        return reportDate.format(formatter);
    }

    public LocalDateTime getReportDate() {
        return reportDate;
    }

    public void setReportDate(LocalDateTime reportDate) {
        this.reportDate = reportDate;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }
}
