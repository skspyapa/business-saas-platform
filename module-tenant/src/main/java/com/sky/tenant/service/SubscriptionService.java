package com.sky.tenant.service;

import com.sky.tenant.dto.SubscriptionResponse;
import com.sky.tenant.entity.Business;
import com.sky.tenant.entity.PricingPlan;
import com.sky.tenant.entity.Subscription;
import com.sky.tenant.exception.BusinessNotFoundException;
import com.sky.tenant.exception.InvalidOperationException;
import com.sky.tenant.mapper.EntityMapper;
import com.sky.tenant.repository.BusinessRepository;
import com.sky.tenant.repository.PricingPlanRepository;
import com.sky.tenant.repository.SubscriptionRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.UUID;

@Service
@Transactional
public class SubscriptionService {

    private final SubscriptionRepository subscriptionRepository;
    private final BusinessRepository businessRepository;
    private final PricingPlanRepository pricingPlanRepository;

    public SubscriptionService(SubscriptionRepository subscriptionRepository,
                               BusinessRepository businessRepository,
                               PricingPlanRepository pricingPlanRepository) {
        this.subscriptionRepository = subscriptionRepository;
        this.businessRepository = businessRepository;
        this.pricingPlanRepository = pricingPlanRepository;
    }

    public SubscriptionResponse assignSubscription(UUID businessId, UUID pricingPlanId) {
        Business business = businessRepository.findById(businessId)
                .orElseThrow(() -> new BusinessNotFoundException("Business with ID '" + businessId + "' not found"));

        PricingPlan plan = pricingPlanRepository.findById(pricingPlanId)
                .orElseThrow(() -> new InvalidOperationException("Pricing plan with ID '" + pricingPlanId + "' not found"));

        if (!plan.getIsActive()) {
            throw new InvalidOperationException("Pricing plan '" + plan.getDisplayName() + "' is not active");
        }

        // Check if already has subscription
        if (subscriptionRepository.findByBusinessId(businessId).isPresent()) {
            // Update existing subscription
            return upgradeSubscription(businessId, pricingPlanId);
        }

        // Create new subscription
        Subscription subscription = EntityMapper.toSubscriptionEntity(business, plan);

        Subscription saved = subscriptionRepository.save(subscription);
        return EntityMapper.toSubscriptionResponse(saved);
    }

    public SubscriptionResponse upgradeSubscription(UUID businessId, UUID newPricingPlanId) {
        Subscription subscription = subscriptionRepository.findByBusinessId(businessId)
                .orElseThrow(() -> new InvalidOperationException("No active subscription found for business with ID '" + businessId + "'"));

        PricingPlan newPlan = pricingPlanRepository.findById(newPricingPlanId)
                .orElseThrow(() -> new InvalidOperationException("Pricing plan with ID '" + newPricingPlanId + "' not found"));

        if (!newPlan.getIsActive()) {
            throw new InvalidOperationException("Pricing plan '" + newPlan.getDisplayName() + "' is not active");
        }

        subscription.setPricingPlan(newPlan);

        Subscription updated = subscriptionRepository.save(subscription);
        return EntityMapper.toSubscriptionResponse(updated);
    }

    public void cancelSubscription(UUID businessId) {
        Subscription subscription = subscriptionRepository.findByBusinessId(businessId)
                .orElseThrow(() -> new InvalidOperationException("No subscription found for business with ID '" + businessId + "'"));

        subscription.setEndDate(LocalDateTime.now());
        subscription.setIsActive(false);

        subscriptionRepository.save(subscription);
    }

    @Transactional(readOnly = true)
    public SubscriptionResponse getActiveSubscription(UUID businessId) {
        Subscription subscription = subscriptionRepository.findByBusinessIdAndIsActiveTrue(businessId)
                .orElseThrow(() -> new InvalidOperationException("No active subscription found for business with ID '" + businessId + "'"));
        return EntityMapper.toSubscriptionResponse(subscription);
    }

    public SubscriptionResponse renewSubscription(UUID businessId) {
        Subscription subscription = subscriptionRepository.findByBusinessIdAndIsActiveTrue(businessId)
                .orElseThrow(() -> new InvalidOperationException("No active subscription found for business with ID '" + businessId + "'"));

        LocalDateTime endDate = subscription.getEndDate();
        if (endDate == null) {
            endDate = LocalDateTime.now();
        }
        // Extend by 30 days or 1 month
        subscription.setEndDate(endDate.plusMonths(1));

        Subscription renewed = subscriptionRepository.save(subscription);
        return EntityMapper.toSubscriptionResponse(renewed);
    }

    @Transactional(readOnly = true)
    public Subscription getSubscriptionEntity(UUID businessId) {
        return subscriptionRepository.findByBusinessId(businessId)
                .orElseThrow(() -> new InvalidOperationException("No subscription found for business with ID '" + businessId + "'"));
    }
}