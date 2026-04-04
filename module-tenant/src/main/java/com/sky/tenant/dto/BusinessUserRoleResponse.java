package com.sky.tenant.dto;

import java.time.LocalDateTime;
import java.util.UUID;

public record BusinessUserRoleResponse(
        UUID id,
        UUID businessId,
        UUID userId,
        String userEmail,
        String userFullName,
        String role,
        Boolean isActive,
        LocalDateTime createdAt,
        LocalDateTime updatedAt
) {
}