package com.mintyfinance.domain.error;

import com.mintyfinance.domain.error.dto.ErrorReportDto;

class ErrorReportDtoMapper {
    static ErrorReportDto map(ErrorReport errorReport) {
        return new ErrorReportDto(
                errorReport.getErrorReportId(),
                errorReport.getUser().getEmail(),
                errorReport.getReportDate(),
                errorReport.getDescription()
        );
    }
}
