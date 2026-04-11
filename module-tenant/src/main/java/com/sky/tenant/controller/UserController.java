package com.sky.tenant.controller;

import com.sky.tenant.dto.UserResponse;
import com.sky.tenant.entity.User;
import com.sky.tenant.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/users")
@Tag(name = "User API", description = "Endpoints for User profile synchronization")
public class UserController {

    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @PostMapping("/sync")
    @Operation(summary = "Sync user with Keycloak", description = "Reads verified OIDC claims from the JWT and automatically registers or updates the User record in the local database. Should be called by the frontend immediately upon initial login/token acquisition.")
    public ResponseEntity<UserResponse> syncUser(@AuthenticationPrincipal Jwt jwt) {
        User syncedUser = userService.syncUserWithKeycloak(jwt);
        
        UserResponse response = new UserResponse(
                syncedUser.getId(),
                syncedUser.getKeycloakId(),
                syncedUser.getEmail(),
                syncedUser.getUsername(),
                syncedUser.getFirstName(),
                syncedUser.getLastName()
        );
        
        return ResponseEntity.ok(response);
    }
}
