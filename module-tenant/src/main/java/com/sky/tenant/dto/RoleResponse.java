package com.sky.tenant.dto;

import com.sky.tenant.enums.PermissionType;
import java.time.LocalDateTime;
import java.util.Set;
import java.util.UUID;

public record RoleResponse(
    UUID id,
    UUID businessId,
    String name,
    String description,
    Boolean isSystemRole,
    Boolean isActive,
    Set<PermissionType> permissions,
    LocalDateTime createdAt,
    LocalDateTime updatedAt
) {}
