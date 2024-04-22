package com.mintyfinance.web;

import com.mintyfinance.config.notification.NotificationService;
import com.mintyfinance.domain.category.CategoryService;
import com.mintyfinance.domain.category.dto.CategoryDto;
import com.mintyfinance.domain.position.PositionService;
import com.mintyfinance.domain.position.dto.PositionDto;
import com.mintyfinance.domain.position.dto.PositionSaveDto;
import com.mintyfinance.domain.user.UserService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.validation.Valid;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

@Controller
@RequestMapping("/position")
public class PositionController {
    private final PositionService positionService;
    private final CategoryService categoryService;
    private final UserService userService;

    private final NotificationService notificationService;

    public PositionController(PositionService positionService, CategoryService categoryService, UserService userService,
                              NotificationService notificationService) {
        this.positionService = positionService;
        this.categoryService = categoryService;
        this.userService = userService;
        this.notificationService = notificationService;
    }

    @GetMapping("/search")
    public String searchPositions(@RequestParam(name = "name") String name, Model model) {
        List<PositionDto> positions = positionService.searchPositionsByName(userService.getCurrentUserId(), name);

        model.addAttribute("heading", "Wyniki wyszukiwania dla frazy '" + name + "'");
        model.addAttribute("description", "Pozycje zgodne z kryteriami wyszukiwania");
        model.addAttribute("positions", positions);
        return "position/position-listing";
    }

    @GetMapping("/add")
    public String addPositionForm(Model model) {
        PositionSaveDto position = new PositionSaveDto();
        position.setPriority((byte) 1);
        position.setRebillTime(LocalTime.of(0, 0));
        position.setIsIncome(true);

        List<CategoryDto> categoriesForUser = categoryService.findAllCategoriesForUser(userService.getCurrentUserId());

        model.addAttribute("categories", categoriesForUser);
        model.addAttribute("minDate", LocalDate.now());
        model.addAttribute("position", position);
        return "position/position-form";
    }

    @PostMapping("/add")
    public String addPosition(@Valid @ModelAttribute("position") PositionSaveDto position, BindingResult result,
                              RedirectAttributes redirectAttributes, Model model) {
        if (result.hasErrors()) {
            model.addAttribute("position", position);
            model.addAttribute("categories", categoryService.findAllCategories());
            return "position/position-form";
        }
//        long startTime = System.nanoTime();

        positionService.addPosition(position);

//        long endTime = System.nanoTime();
//        long duration = (endTime - startTime) / 1_000_000;
//        System.out.println("Usuwanie celu (w kontrolerze) trwało: " + duration + " ms");

        notificationService.add(redirectAttributes, "Pozycja %s została zapisana.", position.getName());
        return "redirect:/user/finance-management";
    }

    @GetMapping("/edit/{id}")
    public String editPositionForm(@PathVariable Long id, Model model) {
        PositionDto position = positionService.validateUserOwnershipOfPositionAndReturnPosition(id);
        List<CategoryDto> categoriesForUser = categoryService.findAllCategoriesForUser(userService.getCurrentUserId());

        model.addAttribute("position", position);
        model.addAttribute("categories", categoriesForUser);
        return "position/edit-position";
    }

    @PostMapping("/edit")
    public String updatePosition(@Valid @ModelAttribute("position") PositionSaveDto position, BindingResult result,
                                 RedirectAttributes redirectAttributes, Model model) {
        if (result.hasErrors()) {
            List<CategoryDto> categoriesForUser =
                    categoryService.findAllCategoriesForUser(userService.getCurrentUserId());
            model.addAttribute("position", position);
            model.addAttribute("categories", categoriesForUser);
            return "position/position-form";
        }

        positionService.updatePosition(position);
        notificationService.add(redirectAttributes, "Pozycja %s została zaktualizowana.", position.getName());
        return "redirect:/";
    }

    @DeleteMapping("/delete/{id}")
    public String deletePosition(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        positionService.deletePosition(id);

        notificationService.add(redirectAttributes, "Pozycja została usunięta.");
        return "redirect:/";
    }
}
