package com.sky.tenant.controller;

import com.sky.tenant.dto.BusinessRequest;
import com.sky.tenant.dto.BusinessResponse;
import com.sky.tenant.service.BusinessService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/v1/businesses")
@Tag(name = "Business", description = "Business management endpoints")
public class BusinessController {

    private final BusinessService businessService;

    public BusinessController(BusinessService businessService) {
        this.businessService = businessService;
    }

    @PostMapping
    @Operation(summary = "Create a new business", description = "Create a new business entity with default subscription (FREE plan)")
    public ResponseEntity<BusinessResponse> createBusiness(
            @RequestBody BusinessRequest request,
            @AuthenticationPrincipal Jwt jwt) {
        // Extract real user ID from Keycloak JWT subject
        UUID ownerId = UUID.fromString(jwt.getSubject()); 
        BusinessResponse response = businessService.createBusiness(request, ownerId);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping("/{businessId}")
    @Operation(summary = "Get business by ID", description = "Retrieve a single business by its unique identifier")
    public ResponseEntity<BusinessResponse> getBusinessById(@PathVariable UUID businessId) {
        BusinessResponse response = businessService.getBusinessById(businessId);
        return ResponseEntity.ok(response);
    }

    @GetMapping
    @Operation(summary = "Get all businesses", description = "Retrieve all businesses with optional search by name")
    public ResponseEntity<Page<BusinessResponse>> getAllBusinesses(
            @RequestParam(required = false) String search,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        Pageable pageable = PageRequest.of(page, Math.min(size, 100)); // Max 100 per page
        Page<BusinessResponse> response = businessService.getAllBusinesses(pageable, search);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/my-businesses")
    @Operation(summary = "Get user's businesses", description = "Retrieve all businesses owned by the current user")
    public ResponseEntity<Page<BusinessResponse>> getMyBusinesses(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @AuthenticationPrincipal Jwt jwt) {
        // Extract real user ID from Keycloak JWT subject
        UUID ownerId = UUID.fromString(jwt.getSubject()); 
        Pageable pageable = PageRequest.of(page, Math.min(size, 100)); // Max 100 per page
        Page<BusinessResponse> response = businessService.getBusinessesByOwner(ownerId, pageable);
        return ResponseEntity.ok(response);
    }

    @PutMapping("/{businessId}")
    @Operation(summary = "Update business", description = "Update business details")
    public ResponseEntity<BusinessResponse> updateBusiness(
            @PathVariable UUID businessId,
            @RequestBody BusinessRequest request) {
        BusinessResponse response = businessService.updateBusiness(businessId, request);
        return ResponseEntity.ok(response);
    }

    @PatchMapping("/{businessId}/activate")
    @Operation(summary = "Activate business", description = "Reactivate a deactivated business")
    public ResponseEntity<BusinessResponse> activateBusiness(@PathVariable UUID businessId) {
        BusinessResponse response = businessService.activateBusiness(businessId);
        return ResponseEntity.ok(response);
    }

    @PatchMapping("/{businessId}/deactivate")
    @Operation(summary = "Deactivate business", description = "Deactivate an active business (soft delete)")
    public ResponseEntity<BusinessResponse> deactivateBusiness(@PathVariable UUID businessId) {
        BusinessResponse response = businessService.deactivateBusiness(businessId);
        return ResponseEntity.ok(response);
    }
}
