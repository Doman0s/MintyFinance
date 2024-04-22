package com.mintyfinance.web;

import com.mintyfinance.config.notification.NotificationService;
import com.mintyfinance.domain.error.ErrorReportService;
import com.mintyfinance.domain.error.dto.ErrorReportSaveDto;
import com.mintyfinance.domain.user.UserService;
import com.mintyfinance.domain.user.dto.UserDto;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.validation.Valid;

@Controller
@RequestMapping("/error-report")
public class ErrorReportController {
    private final ErrorReportService errorReportService;
    private final NotificationService notificationService;

    public ErrorReportController(ErrorReportService errorReportService, NotificationService notificationService) {
        this.errorReportService = errorReportService;
        this.notificationService = notificationService;
    }

    @GetMapping
    public String errorReportForm(Model model) {
        ErrorReportSaveDto errorReport = new ErrorReportSaveDto();

        model.addAttribute("error", errorReport);
        return "error/error-report-form";
    }

    @PostMapping
    public String saveErrorReport(@Valid @ModelAttribute("error") ErrorReportSaveDto errorReportSaveDto,
                           BindingResult result, RedirectAttributes redirectAttributes, Model model) {
        if (result.hasErrors()) {
            model.addAttribute("error", errorReportSaveDto);
            return "error/error-report-form";
        }
        errorReportService.saveErrorReport(errorReportSaveDto);

        notificationService.add(redirectAttributes, "Pomyślnie zgłoszono błąd.");
        return "redirect:/error-report";
    }
}
