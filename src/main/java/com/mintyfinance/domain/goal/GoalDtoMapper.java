package com.mintyfinance.domain.goal;

import com.mintyfinance.domain.goal.dto.GoalDto;

class GoalDtoMapper {
    static GoalDto map(Goal goal) {
        return new GoalDto(
                goal.getGoalId(),
                goal.getUser(),
                goal.getName(),
                goal.getAmount(),
                goal.isCompleted()
        );
    }
}
