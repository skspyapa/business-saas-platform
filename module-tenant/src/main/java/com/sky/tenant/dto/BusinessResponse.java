package com.sky.tenant.dto;

import java.time.LocalDateTime;
import java.util.UUID;

public record BusinessResponse(
        UUID id,
        String name,
        String businessType,
        String description,
        String subdomain,
        String logoUrl,
        String websiteUrl,
        String country,
        String city,
        String address,
        Boolean isActive,
        LocalDateTime createdAt,
        LocalDateTime updatedAt,
        String createdBy
) {
}