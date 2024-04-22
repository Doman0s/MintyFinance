package com.mintyfinance.domain.user;

import com.mintyfinance.domain.user.dto.UserCredentialsDto;
import com.mintyfinance.domain.user.dto.UserDto;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Set;
import java.util.stream.Collectors;

class UserDtoMapper {
    static UserDto map(User user) {
        Long userId = user.getUserId();
        String firstName = user.getFirstName();
        String lastName = user.getLastName();
        LocalDate dateOfBirth = user.getDateOfBirth();
        char gender = user.getGender();
        BigDecimal balance = user.getBalance();
        String email = user.getEmail();
        String password = user.getPassword();
        Set<String> roles = user.getRoles()
                .stream()
                .map(UserRole::getName)
                .collect(Collectors.toSet());
        return new UserDto(userId, firstName, lastName, dateOfBirth, gender, balance, email,
                password, roles);
    }
}
