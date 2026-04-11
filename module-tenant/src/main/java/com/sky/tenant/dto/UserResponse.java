package com.sky.tenant.dto;

import java.util.UUID;

public record UserResponse(
        UUID id,
        UUID keycloakId,
        String email,
        String username,
        String firstName,
        String lastName
) {
}
