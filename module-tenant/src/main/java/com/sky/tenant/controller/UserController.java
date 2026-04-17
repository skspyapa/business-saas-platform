package com.sky.tenant.controller;

import com.sky.tenant.dto.UserResponse;
import com.sky.tenant.entity.User;
import com.sky.tenant.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

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
        
        return ResponseEntity.ok(mapToResponse(syncedUser));
    }

    @GetMapping("/me")
    @Operation(summary = "Get current user profile", description = "Retrieves the profile of the currently logged in user based on their JWT.")
    public ResponseEntity<UserResponse> getCurrentUser(@AuthenticationPrincipal Jwt jwt) {
        User user = userService.getCurrentUser(jwt);
        return ResponseEntity.ok(mapToResponse(user));
    }

    @GetMapping
    @Operation(summary = "Get all users", description = "Retrieves a list of all signed-up users in the platform.")
    public ResponseEntity<List<UserResponse>> getAllUsers() {
        List<User> users = userService.getAllUsers();
        List<UserResponse> response = users.stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
        return ResponseEntity.ok(response);
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get user by ID", description = "Retrieves a specific user profile by their internal system UUID.")
    public ResponseEntity<UserResponse> getUserById(@PathVariable UUID id) {
        User user = userService.getUserById(id);
        return ResponseEntity.ok(mapToResponse(user));
    }

    private UserResponse mapToResponse(User user) {
        return new UserResponse(
                user.getId(),
                user.getKeycloakId(),
                user.getEmail(),
                user.getUsername(),
                user.getFirstName(),
                user.getLastName()
        );
    }
}
