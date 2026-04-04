package com.sky.tenant.controller;

import com.sky.tenant.dto.SubscriptionRequest;
import com.sky.tenant.dto.SubscriptionResponse;
import com.sky.tenant.service.SubscriptionService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/v1/businesses/{businessId}/subscription")
@Tag(name = "Subscription", description = "Subscription management endpoints")
public class SubscriptionController {

    private final SubscriptionService subscriptionService;

    public SubscriptionController(SubscriptionService subscriptionService) {
        this.subscriptionService = subscriptionService;
    }

    @PostMapping
    @Operation(summary = "Assign subscription", description = "Assign or upgrade a subscription plan for a business")
    public ResponseEntity<SubscriptionResponse> assignSubscription(
            @PathVariable UUID businessId,
            @RequestBody SubscriptionRequest request) {
        SubscriptionResponse response = subscriptionService.assignSubscription(businessId, request.pricingPlanId());
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping
    @Operation(summary = "Get active subscription", description = "Retrieve the active subscription for a business")
    public ResponseEntity<SubscriptionResponse> getActiveSubscription(@PathVariable UUID businessId) {
        SubscriptionResponse response = subscriptionService.getActiveSubscription(businessId);
        return ResponseEntity.ok(response);
    }

    @PutMapping
    @Operation(summary = "Upgrade subscription", description = "Upgrade to a different pricing plan")
    public ResponseEntity<SubscriptionResponse> upgradeSubscription(
            @PathVariable UUID businessId,
            @RequestBody SubscriptionRequest request) {
        SubscriptionResponse response = subscriptionService.upgradeSubscription(businessId, request.pricingPlanId());
        return ResponseEntity.ok(response);
    }

    @PostMapping("/renew")
    @Operation(summary = "Renew subscription", description = "Renew the active subscription for an additional period")
    public ResponseEntity<SubscriptionResponse> renewSubscription(@PathVariable UUID businessId) {
        SubscriptionResponse response = subscriptionService.renewSubscription(businessId);
        return ResponseEntity.ok(response);
    }

    @DeleteMapping
    @Operation(summary = "Cancel subscription", description = "Cancel the active subscription for a business")
    public ResponseEntity<Void> cancelSubscription(@PathVariable UUID businessId) {
        subscriptionService.cancelSubscription(businessId);
        return ResponseEntity.noContent().build();
    }
}
