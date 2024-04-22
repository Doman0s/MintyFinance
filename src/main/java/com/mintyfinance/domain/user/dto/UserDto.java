package com.mintyfinance.domain.user.dto;

import org.springframework.format.annotation.DateTimeFormat;

import javax.validation.constraints.*;
import java.math.BigDecimal;
import java.sql.Date;
import java.time.LocalDate;
import java.util.Set;

public class UserDto {
    private Long userId;
    @NotBlank @Size(min=2, max=30)
    private String firstName;
    @NotBlank @Size(min=2, max=40)
    private String lastName;
    @DateTimeFormat(pattern="yyyy-MM-dd") @NotNull @Past
    private LocalDate dateOfBirth;
    private char gender;
    @Digits(integer = 9, fraction = 2)
    private BigDecimal balance;
    @NotNull @Email(regexp=".+@.+\\..+") @Size(max = 100)
    private String email;
    private String password;
    private final Set<String> roles;

    public UserDto(Long userId, String firstName, String lastName, LocalDate dateOfBirth, char gender,
                   BigDecimal balance, String email, String password, Set<String> roles) {
        this.userId = userId;
        this.firstName = firstName;
        this.lastName = lastName;
        this.dateOfBirth = dateOfBirth;
        this.gender = gender;
        this.balance = balance;
        this.email = email;
        this.password = password;
        this.roles = roles;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public LocalDate getDateOfBirth() {
        return dateOfBirth;
    }

    public void setDateOfBirth(LocalDate dateOfBirth) {
        this.dateOfBirth = dateOfBirth;
    }

    public char getGender() {
        return gender;
    }

    public void setGender(char gender) {
        this.gender = gender;
    }

    public BigDecimal getBalance() {
        return balance;
    }

    public void setBalance(BigDecimal balance) {
        this.balance = balance;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public Set<String> getRoles() {
        return roles;
    }
}