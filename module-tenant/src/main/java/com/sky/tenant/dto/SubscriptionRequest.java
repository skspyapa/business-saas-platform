package com.sky.tenant.dto;

import java.util.UUID;

public record SubscriptionRequest(
        UUID pricingPlanId
) {
}