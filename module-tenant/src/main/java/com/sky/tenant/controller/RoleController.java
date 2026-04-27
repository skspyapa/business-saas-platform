package com.sky.tenant.controller;

import com.sky.tenant.dto.RoleRequest;
import com.sky.tenant.dto.RoleResponse;
import com.sky.tenant.service.RoleService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/v1/businesses/{businessId}/roles")
@RequiredArgsConstructor
@Tag(name = "Roles", description = "Role and permission management endpoints")
public class RoleController {

    private final RoleService roleService;

    @PostMapping
    @Operation(summary = "Create a new role", description = "Create a custom role with specific permissions for a business")
    public ResponseEntity<RoleResponse> createRole(
            @PathVariable UUID businessId,
            @RequestBody RoleRequest request) {
        return new ResponseEntity<>(roleService.createRole(businessId, request), HttpStatus.CREATED);
    }

    @GetMapping
    @Operation(summary = "Get business roles", description = "Retrieve a paginated list of all roles within a specific business")
    public ResponseEntity<Page<RoleResponse>> getRoles(
            @PathVariable UUID businessId,
            Pageable pageable) {
        return ResponseEntity.ok(roleService.getRolesForBusiness(businessId, pageable));
    }

    @PutMapping("/{roleId}")
    @Operation(summary = "Update a role", description = "Modify the name, description, or permissions of an existing role")
    public ResponseEntity<RoleResponse> updateRole(
            @PathVariable UUID businessId,
            @PathVariable UUID roleId,
            @RequestBody RoleRequest request) {
        return ResponseEntity.ok(roleService.updateRole(businessId, roleId, request));
    }

    @PatchMapping("/{roleId}/status")
    @Operation(summary = "Toggle role status", description = "Activate or deactivate a role")
    public ResponseEntity<Void> toggleRoleStatus(
            @PathVariable UUID businessId,
            @PathVariable UUID roleId,
            @RequestParam boolean isActive) {
        roleService.toggleRoleStatus(businessId, roleId, isActive);
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("/{roleId}")
    @Operation(summary = "Delete a role", description = "Permanently remove a custom role from a business")
    public ResponseEntity<Void> deleteRole(
            @PathVariable UUID businessId,
            @PathVariable UUID roleId) {
        roleService.deleteRole(businessId, roleId);
        return ResponseEntity.noContent().build();
    }
}
