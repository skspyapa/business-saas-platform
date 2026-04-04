package com.sky.tenant.controller;

import com.sky.tenant.dto.BusinessUserRoleRequest;
import com.sky.tenant.dto.BusinessUserRoleResponse;
import com.sky.tenant.service.BusinessUserRoleService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/v1/businesses/{businessId}/users")
@Tag(name = "Business User Roles", description = "Business user role management endpoints")
public class BusinessUserRoleController {

    private final BusinessUserRoleService businessUserRoleService;

    public BusinessUserRoleController(BusinessUserRoleService businessUserRoleService) {
        this.businessUserRoleService = businessUserRoleService;
    }

    @PostMapping
    @Operation(summary = "Assign user to business", description = "Assign a user to a business with a specific role (OWNER, ADMIN, MANAGER, STAFF)")
    public ResponseEntity<BusinessUserRoleResponse> assignUserToBusinessWithRole(
            @PathVariable UUID businessId,
            @RequestBody BusinessUserRoleRequest request) {
        BusinessUserRoleResponse response = businessUserRoleService.assignUserToBusinessWithRole(
                businessId, request.userId(), request.role());
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping
    @Operation(summary = "List business users", description = "Retrieve all users assigned to a business with pagination")
    public ResponseEntity<Page<BusinessUserRoleResponse>> getUsersForBusiness(
            @PathVariable UUID businessId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        Pageable pageable = PageRequest.of(page, Math.min(size, 100)); // Max 100 per page
        Page<BusinessUserRoleResponse> response = businessUserRoleService.getUsersForBusiness(businessId, pageable);
        return ResponseEntity.ok(response);
    }

    @PutMapping("/{userId}")
    @Operation(summary = "Update user role", description = "Update the role of a user in a business")
    public ResponseEntity<BusinessUserRoleResponse> updateUserRole(
            @PathVariable UUID businessId,
            @PathVariable UUID userId,
            @RequestBody BusinessUserRoleRequest request) {
        BusinessUserRoleResponse response = businessUserRoleService.updateUserRole(businessId, userId, request.role());
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/{userId}")
    @Operation(summary = "Remove user from business", description = "Remove a user from a business (deactivate their role)")
    public ResponseEntity<Void> removeUserFromBusiness(
            @PathVariable UUID businessId,
            @PathVariable UUID userId) {
        businessUserRoleService.removeUserFromBusiness(businessId, userId);
        return ResponseEntity.noContent().build();
    }
}
