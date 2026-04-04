package com.sky.tenant.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

public record SubscriptionResponse(
        UUID id,
        UUID businessId,
        UUID pricingPlanId,
        String planType,
        String planName,
        BigDecimal monthlyPrice,
        Integer maxUsers,
        Integer maxStorageGb,
        LocalDateTime startDate,
        LocalDateTime endDate,
        Boolean isActive,
        LocalDateTime createdAt,
        LocalDateTime updatedAt
) {
}