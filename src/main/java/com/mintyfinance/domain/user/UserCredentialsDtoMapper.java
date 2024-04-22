package com.mintyfinance.domain.user;

import com.mintyfinance.domain.user.dto.UserCredentialsDto;

import java.sql.Date;
import java.time.LocalDate;
import java.util.Set;
import java.util.stream.Collectors;

class UserCredentialsDtoMapper {
    static UserCredentialsDto map(User user) {
        Long userId = user.getUserId();
        String email = user.getEmail();
        String password = user.getPassword();
        Set<String> roles = user.getRoles()
                .stream()
                .map(UserRole::getName)
                .collect(Collectors.toSet());
        return new UserCredentialsDto(userId, email, password, roles);
    }
}
