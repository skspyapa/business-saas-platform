package com.sky.tenant.dto;

import java.util.UUID;

public record BusinessUserRoleRequest(
        UUID userId,
        String role
) {
}