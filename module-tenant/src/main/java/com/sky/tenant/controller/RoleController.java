package com.sky.tenant.controller;

import com.sky.tenant.dto.RoleRequest;
import com.sky.tenant.dto.RoleResponse;
import com.sky.tenant.service.RoleService;
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
public class RoleController {

    private final RoleService roleService;

    @PostMapping
    public ResponseEntity<RoleResponse> createRole(
            @PathVariable UUID businessId,
            @RequestBody RoleRequest request) {
        return new ResponseEntity<>(roleService.createRole(businessId, request), HttpStatus.CREATED);
    }

    @GetMapping
    public ResponseEntity<Page<RoleResponse>> getRoles(
            @PathVariable UUID businessId,
            Pageable pageable) {
        return ResponseEntity.ok(roleService.getRolesForBusiness(businessId, pageable));
    }

    @PutMapping("/{roleId}")
    public ResponseEntity<RoleResponse> updateRole(
            @PathVariable UUID businessId,
            @PathVariable UUID roleId,
            @RequestBody RoleRequest request) {
        return ResponseEntity.ok(roleService.updateRole(businessId, roleId, request));
    }

    @PatchMapping("/{roleId}/status")
    public ResponseEntity<Void> toggleRoleStatus(
            @PathVariable UUID businessId,
            @PathVariable UUID roleId,
            @RequestParam boolean isActive) {
        roleService.toggleRoleStatus(businessId, roleId, isActive);
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("/{roleId}")
    public ResponseEntity<Void> deleteRole(
            @PathVariable UUID businessId,
            @PathVariable UUID roleId) {
        roleService.deleteRole(businessId, roleId);
        return ResponseEntity.noContent().build();
    }
}
