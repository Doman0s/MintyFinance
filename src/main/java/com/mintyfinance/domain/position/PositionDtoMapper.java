package com.mintyfinance.domain.position;

import com.mintyfinance.domain.position.dto.PositionDto;

class PositionDtoMapper {
    static PositionDto map(Position position) {
        return new PositionDto(
                position.getPositionId(),
                position.getCategory().getName(),
                position.getUser().getUserId(),
                position.getName(),
                position.getDescription(),
                position.isIncome(),
                position.getPriority(),
                position.getAmount(),
                position.getRebillDate(),
                position.getRecurrenceType()
        );
    }
}
