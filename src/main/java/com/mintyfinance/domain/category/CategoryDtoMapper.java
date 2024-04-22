package com.mintyfinance.domain.category;

import com.mintyfinance.domain.category.dto.CategoryDto;

class CategoryDtoMapper {
    static CategoryDto map(Category category) {
        return new CategoryDto(
                category.getCategoryId(),
                category.getUser().getUserId(),
                category.getName(),
                category.getDescription()
        );
    }
}
