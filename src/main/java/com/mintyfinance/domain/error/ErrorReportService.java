package com.mintyfinance.domain.error;

import com.mintyfinance.domain.error.dto.ErrorReportDto;
import com.mintyfinance.domain.error.dto.ErrorReportSaveDto;
import com.mintyfinance.domain.exception.UserNotFoundException;
import com.mintyfinance.domain.user.User;
import com.mintyfinance.domain.user.UserRepository;
import com.mintyfinance.domain.user.UserService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

@Service
public class ErrorReportService {
    private final ErrorReportRepository errorReportRepository;
    private final UserService userService;
    private final UserRepository userRepository;

    public ErrorReportService(ErrorReportRepository errorReportRepository, UserService userService,
                              UserRepository userRepository) {
        this.errorReportRepository = errorReportRepository;
        this.userService = userService;
        this.userRepository = userRepository;
    }

    public List<ErrorReport> findErrorsByEmail(String email) {
        Optional<User> userOptional = userRepository.findByEmail(email);
        if(userOptional.isPresent()) {
            return errorReportRepository.findAllByUser(userOptional.get());
        }
        return Collections.emptyList();
    }

    public List<ErrorReportDto> findAllErrors() {
        return errorReportRepository.findAll().stream().map(ErrorReportDtoMapper::map).toList();
    }

    @Transactional
    public void saveErrorReport(ErrorReportSaveDto errorReportSaveDto) {
        ErrorReport errorReport = new ErrorReport();
        User user = userRepository.findById(userService.getCurrentUserId())
                .orElseThrow(() -> new UserNotFoundException(String
                        .format("Użytkownik o id %d nie został znaleziony", userService.getCurrentUserId())
                ));
        errorReport.setUser(user);
        errorReport.setReportDate(LocalDateTime.now());
        errorReport.setDescription(errorReportSaveDto.getDescription());
        errorReportRepository.save(errorReport);
    }

    public void deleteErrorReport(Long id) {
        errorReportRepository.deleteById(id);
    }
}
