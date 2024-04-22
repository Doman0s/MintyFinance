package com.mintyfinance.domain.category;

import com.mintyfinance.domain.user.User;
import org.springframework.data.repository.CrudRepository;

import java.util.List;
import java.util.Optional;

public interface CategoryRepository extends CrudRepository<Category, Long> {
    Optional<Category> findByNameIgnoreCase(String name);
    List<Category> findAllByUser_UserIdOrUser_UserId(Long userId, Long defaultId);
    List<Category> findAllByUser_UserId(Long userId);
    Optional<Category> findByNameAndUser_UserId(String name, Long userId);
    void deleteAllByUser(User user);
}
