package com.sky.tenant.controller;

import com.sky.tenant.dto.BusinessSettingsRequest;
import com.sky.tenant.dto.BusinessSettingsResponse;
import com.sky.tenant.service.BusinessSettingsService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/v1/businesses/{businessId}/settings")
@Tag(name = "Business Settings", description = "Business settings management endpoints")
public class BusinessSettingsController {

    private final BusinessSettingsService businessSettingsService;

    public BusinessSettingsController(BusinessSettingsService businessSettingsService) {
        this.businessSettingsService = businessSettingsService;
    }

    @GetMapping
    @Operation(summary = "Get business settings", description = "Retrieve the current settings for a business")
    public ResponseEntity<BusinessSettingsResponse> getBusinessSettings(@PathVariable UUID businessId) {
        BusinessSettingsResponse response = businessSettingsService.getBusinessSettings(businessId);
        return ResponseEntity.ok(response);
    }

    @PutMapping
    @Operation(summary = "Update business settings", description = "Update timezone, currency, and custom feature settings for a business")
    public ResponseEntity<BusinessSettingsResponse> updateBusinessSettings(
            @PathVariable UUID businessId,
            @RequestBody BusinessSettingsRequest request) {
        BusinessSettingsResponse response = businessSettingsService.updateBusinessSettings(businessId, request);
        return ResponseEntity.ok(response);
    }
}
