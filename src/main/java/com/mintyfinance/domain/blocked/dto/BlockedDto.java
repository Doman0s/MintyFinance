package com.mintyfinance.domain.blocked.dto;

import java.time.LocalDateTime;

public class BlockedDto {
    private Long blockedId;
    private Long userId;
    private LocalDateTime blockDate;

    public BlockedDto() {
    }

    public BlockedDto(Long blockedId, Long userId) {
        this.blockedId = blockedId;
        this.userId = userId;
        this.blockDate = LocalDateTime.now();
    }

    public Long getBlockedId() {
        return blockedId;
    }

    public void setBlockedId(Long blockedId) {
        this.blockedId = blockedId;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public LocalDateTime getBlockDate() {
        return blockDate;
    }

    public void setBlockDate(LocalDateTime blockDate) {
        this.blockDate = blockDate;
    }
}
