package com.mintyfinance.domain.user;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {
    List<User> findAll();
    Optional<User> findByEmail(String email);
    Optional<User> findByUserId(Long id);
    List<User> findAllByUserIdIn(List<Long> ids);
    List<User> findAllByUserIdNotIn(List<Long> ids);
}
