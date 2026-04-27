package com.sky.tenant.dto;

import com.sky.tenant.enums.PermissionType;
import java.util.Set;

public record RoleRequest(
    String name,
    String description,
    Set<PermissionType> permissions
) {}
