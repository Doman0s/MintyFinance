package com.mintyfinance.domain.category;

import com.mintyfinance.domain.category.dto.CategoryDto;
import com.mintyfinance.domain.exception.CantDeleteCategoryWithPositions;
import com.mintyfinance.domain.exception.CategoryNotFoundException;
import com.mintyfinance.domain.exception.ForbiddenAccessException;
import com.mintyfinance.domain.exception.UserNotFoundException;
import com.mintyfinance.domain.position.PositionRepository;
import com.mintyfinance.domain.position.PositionService;
import com.mintyfinance.domain.position.dto.PositionDto;
import com.mintyfinance.domain.user.User;
import com.mintyfinance.domain.user.UserRepository;
import com.mintyfinance.domain.user.UserService;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.StreamSupport;

@Service
public class CategoryService {
    private final CategoryRepository categoryRepository;
    private final UserRepository userRepository;
    private final UserService userService;
    private final PositionRepository positionRepository;
    private final PositionService positionService;

    private static final Long ADMIN_ID = 1L;

    public CategoryService(CategoryRepository categoryRepository, UserRepository userRepository,
                           UserService userService, PositionRepository positionRepository,
                           PositionService positionService) {
        this.categoryRepository = categoryRepository;
        this.userRepository = userRepository;
        this.userService = userService;
        this.positionRepository = positionRepository;
        this.positionService = positionService;
    }

    public void validateIfUserCanEditThisCategory(Long userIdFromCategory) {
        Long userId = userService.getCurrentUserId();
        if (!userIdFromCategory.equals(userId) && !userService.isAdmin(userId)) {
            throw new ForbiddenAccessException("Brak dostępu");
        }
    }

    public void validateIfUserCanSeeThisCategory(Long userIdFromCategory) {
        Long userId = userService.getCurrentUserId();
        if (!userIdFromCategory.equals(ADMIN_ID) && !userIdFromCategory.equals(userId)) {
            throw new ForbiddenAccessException("Brak dostępu");
        }
    }

    public List<PositionDto> findPositionsByCategoryId(Long categoryId) {
        return positionService.findPositionsByUserIdAndCategory_Id(userService.getCurrentUserId(), categoryId);
    }

    public List<PositionDto> findPositionsByCategoryIdToDelete(Long categoryId) {
        return positionService.findPositionsByCategory_Id(categoryId);
    }

    public List<CategoryDto> findAllCategoriesForUser(Long userId) {
        List<Category> defaultCategories = categoryRepository.findAllByUser_UserId(ADMIN_ID);
        if (!userId.equals(ADMIN_ID)) {
            List<Category> userCategories = categoryRepository.findAllByUser_UserId(userId);
            defaultCategories.addAll(userCategories);
        }
        return defaultCategories.stream().map(CategoryDtoMapper::map).toList();
    }

    public Optional<CategoryDto> findCategoryById(Long id) {
        return categoryRepository.findById(id)
                .map(CategoryDtoMapper::map);
    }

    public List<CategoryDto> findAllCategories() {
        return StreamSupport.stream(categoryRepository.findAll().spliterator(), false)
                .map(CategoryDtoMapper::map)
                .toList();
    }

    public boolean isCategoryNameUniqueForUser(Long userId, String categoryName) {
        return categoryRepository.findByNameAndUser_UserId(categoryName, userId).isEmpty();
    }

    public boolean checkIfCategoryNameExistsForCurrentUser(CategoryDto category) {
        List<CategoryDto> allCategoriesForUser = findAllCategoriesForUser(userService.getCurrentUserId());
        return allCategoriesForUser.stream()
                .anyMatch(existingCategory -> existingCategory.getName().equalsIgnoreCase(category.getName()));
    }

    @Transactional
    public void addCategory(CategoryDto category) {
        Category categoryToSave = new Category();
        categoryToSave.setName(category.getName());
        categoryToSave.setDescription(category.getDescription());
        User user = userRepository.findById(userService.getCurrentUserId())
                .orElseThrow(() -> new UserNotFoundException(
                        "Nie znaleziono użytkownika o id " + userService.getCurrentUserId())
                );
        categoryToSave.setUser(user);
        categoryRepository.save(categoryToSave);
    }

    @Transactional
    public void updateCategory(CategoryDto updateCategory) {
        Category category = categoryRepository.findById(updateCategory.getCategoryId())
                .orElseThrow(() -> new CategoryNotFoundException(
                        "Nie znaleziono kategorii o id " + updateCategory.getCategoryId())
                );
        category.setName(updateCategory.getName());
        category.setDescription(updateCategory.getDescription());
        User user = userRepository.findById(updateCategory.getUserId())
                .orElseThrow(() -> new UserNotFoundException(
                        "Nie znaleziono użytkownika o id " + updateCategory.getUserId())
                );
        category.setUser(user);
        categoryRepository.save(category);
    }

    @Transactional
    public void deleteCategory(Long id) {
        Category category = categoryRepository.findById(id)
                .orElseThrow(() -> new CategoryNotFoundException("Nie znaleziono kategorii o id " + id));

        List<PositionDto> relatedPositions = findPositionsByCategoryIdToDelete(id);
        if (!relatedPositions.isEmpty()) {
            throw new CantDeleteCategoryWithPositions("Nie można usunąć kategorii, która ma powiązane pozycje.");
        }

        categoryRepository.delete(category);
    }
}
