package com.sky.tenant.dto;

public record BusinessSettingsRequest(
        String timezone,
        String currency,
        String settings
) {
}