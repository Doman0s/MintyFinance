package com.mintyfinance.domain.error;

import com.mintyfinance.domain.error.dto.ErrorReportDto;
import com.mintyfinance.domain.user.User;
import com.mintyfinance.domain.user.dto.UserCredentialsDto;
import com.mintyfinance.domain.user.dto.UserDto;
import org.springframework.data.repository.CrudRepository;

import java.util.List;

public interface ErrorReportRepository extends CrudRepository<ErrorReport, Long> {
    List<ErrorReport> findAll();
    List<ErrorReport> findAllByUser(User user);
    void deleteById(Long id);
    void deleteAllByUser(User user);
}
