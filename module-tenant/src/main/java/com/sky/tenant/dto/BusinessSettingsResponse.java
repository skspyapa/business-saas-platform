package com.sky.tenant.dto;

import java.time.LocalDateTime;
import java.util.UUID;

public record BusinessSettingsResponse(
        UUID id,
        UUID businessId,
        String settings,
        LocalDateTime createdAt,
        LocalDateTime updatedAt
) {
}