package com.mintyfinance.domain.blocked;

import com.mintyfinance.domain.blocked.dto.BlockedDto;

class BlockedDtoMapper {
    static BlockedDto map(Blocked blocked) {
        return new BlockedDto(
                blocked.getBlockedId(),
                blocked.getUser().getUserId()
        );
    }
}