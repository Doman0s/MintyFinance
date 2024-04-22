package com.mintyfinance.domain.category.dto;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;

public class CategoryDto {
    private Long categoryId;
    private Long userId;
    @NotBlank @Size(max = 100)
    private String name;
    @NotBlank @Size(max = 500)
    private String description;

    public CategoryDto() {
    }

    public CategoryDto(Long categoryId, Long userId, String name, String description) {
        this.categoryId = categoryId;
        this.userId = userId;
        this.name = name;
        this.description = description;
    }

    public Long getCategoryId() {
        return categoryId;
    }

    public void setCategoryId(Long categoryId) {
        this.categoryId = categoryId;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }
}
