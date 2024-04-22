package com.mintyfinance.domain.blocked;

import org.springframework.data.repository.CrudRepository;

import java.util.List;

public interface BlockedRepository extends CrudRepository<Blocked, Long> {
    List<Blocked> findAll();
    boolean existsByUser_UserId(Long userId);
    void deleteByUser_UserId(Long userId);
}
