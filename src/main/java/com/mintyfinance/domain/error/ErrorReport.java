package com.mintyfinance.domain.error;

import com.mintyfinance.domain.user.User;

import javax.persistence.*;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@Entity
@Table(name = "error_report")
public class ErrorReport {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "error_report_id")
    private Long errorReportId;
    @ManyToOne
    @JoinColumn(name = "users_id", referencedColumnName = "users_id")
    private User user;
    private LocalDateTime reportDate;
    @NotBlank @Size(max = 500)
    private String description;

    public Long getErrorReportId() {
        return errorReportId;
    }

    public void setErrorReportId(Long errorReportId) {
        this.errorReportId = errorReportId;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
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
