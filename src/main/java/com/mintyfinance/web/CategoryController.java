package com.mintyfinance.web;

import com.mintyfinance.config.notification.NotificationService;
import com.mintyfinance.domain.category.Category;
import com.mintyfinance.domain.category.CategoryService;
import com.mintyfinance.domain.category.dto.CategoryDto;
import com.mintyfinance.domain.exception.CategoryNotFoundException;

import com.mintyfinance.domain.position.dto.PositionDto;
import com.mintyfinance.domain.user.UserService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.validation.Valid;
import java.util.List;

@Controller
@RequestMapping("/category")
public class CategoryController {
    private final CategoryService categoryService;
    private final UserService userService;
    public final NotificationService notificationService;

    public CategoryController(CategoryService categoryService, UserService userService,
                              NotificationService notificationService) {
        this.categoryService = categoryService;
        this.userService = userService;
        this.notificationService = notificationService;
    }

    @GetMapping("/categories")
    public String getCategoryList(Model model) {
        Long currentUserId = userService.getCurrentUserId();
        List<CategoryDto> categories = categoryService.findAllCategoriesForUser(currentUserId);

        model.addAttribute("categories", categories);
        model.addAttribute("isAdmin", userService.isAdmin(currentUserId));
        return "category/category-listing";
    }

    @GetMapping("/{id}")
    public String getCategory(@PathVariable Long id, Model model) {
        CategoryDto category = categoryService.findCategoryById(id)
                .orElseThrow(() -> new CategoryNotFoundException("Nie znaleziono kategorii o id " + id));

        categoryService.validateIfUserCanSeeThisCategory(category.getUserId());
        List<PositionDto> positions = categoryService.findPositionsByCategoryId(id);

        model.addAttribute("heading", category.getName());
        model.addAttribute("description", category.getDescription());
        model.addAttribute("positions", positions);
        return "category/category-position-listing";
    }

    @GetMapping("/add")
    public String addCategoryForm(Model model) {
        model.addAttribute("category", new CategoryDto());
        return "category/category-form";
    }

    @PostMapping("/add")
    public String addCategory(@Valid @ModelAttribute("category") CategoryDto category, BindingResult result,
                              RedirectAttributes redirectAttributes, Model model) {
        if (result.hasErrors()) {
            model.addAttribute("category", category);
            return "category/category-form";
        }

        boolean categoryAlreadyExists = categoryService.checkIfCategoryNameExistsForCurrentUser(category);
        if (categoryAlreadyExists) {
            model.addAttribute("errorMessage", String.format("Kategoria '%s' już istnieje.",
                    category.getName()));
            return "category/category-form";
        }

        categoryService.addCategory(category);
        notificationService.add(redirectAttributes, "Kategoria '%s' została zapisana", category.getName());
        return "redirect:/user/finance-management";
    }

    @GetMapping("/edit/{id}")
    public String editCategoryForm(@PathVariable Long id, Model model) {
        CategoryDto category = categoryService.findCategoryById(id)
                .orElseThrow(() -> new CategoryNotFoundException("Nie znaleziono kategorii o id " + id));
        categoryService.validateIfUserCanEditThisCategory(category.getUserId());

        model.addAttribute("category", category);
        return "category/edit-category";
    }

    @PostMapping("/edit")
    public String updateCategory(@Valid @ModelAttribute("category") CategoryDto category, BindingResult result,
                                 RedirectAttributes redirectAttributes, Model model) {
        if (result.hasErrors()) {
            model.addAttribute("category", category);
            return "category/edit-category";
        }

        if (!categoryService.isCategoryNameUniqueForUser(category.getUserId(), category.getName())) {
            model.addAttribute("errorMessage", "Kategoria o nazwie " + category.getName() +
                    " już istnieje");
            model.addAttribute("category", category);
            return "category/edit-category";
        }

        categoryService.updateCategory(category);

        notificationService.add(redirectAttributes, "Dane kategorii '%s' zostały zaktualizowane",
                category.getName());
        return "redirect:/category/categories";
    }

    @DeleteMapping("/delete/{id}")
    public String deleteCategory(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        categoryService.deleteCategory(id);

        notificationService.add(redirectAttributes, "Kategoria została usunięta.");
        return "redirect:/category/categories";
    }
}
