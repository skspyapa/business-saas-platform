package com.sky.tenant.mapper;

import com.sky.tenant.dto.*;
import com.sky.tenant.entity.*;

public class EntityMapper {

    public static BusinessResponse toBusinessResponse(Business business) {
        if (business == null) {
            return null;
        }
        return new BusinessResponse(
                business.getId(),
                business.getName(),
                business.getBusinessType(),
                business.getDescription(),
                business.getSubdomain(),
                business.getLogoUrl(),
                business.getWebsiteUrl(),
                business.getCountry(),
                business.getCity(),
                business.getAddress(),
                business.getIsActive(),
                business.getCreatedAt(),
                business.getUpdatedAt(),
                business.getCreatedBy()
        );
    }

    public static Business toBusinessEntity(BusinessRequest request, User owner) {
        if (request == null) {
            return null;
        }
        Business business = new Business();
        business.setName(request.name());
        business.setBusinessType(request.businessType());
        business.setDescription(request.description());
        business.setSubdomain(request.subdomain());
        business.setLogoUrl(request.logoUrl());
        business.setWebsiteUrl(request.websiteUrl());
        business.setCountry(request.country());
        business.setCity(request.city());
        business.setAddress(request.address());
        business.setOwner(owner);
        business.setIsActive(true);
        return business;
    }

    public static SubscriptionResponse toSubscriptionResponse(Subscription subscription) {
        if (subscription == null) {
            return null;
        }
        PricingPlan plan = subscription.getPricingPlan();
        return new SubscriptionResponse(
                subscription.getId(),
                subscription.getBusiness().getId(),
                plan.getId(),
                plan.getPlanType(),
                plan.getDisplayName(),
                plan.getMonthlyPrice(),
                plan.getMaxUsers(),
                plan.getMaxStorageGb(),
                subscription.getStartDate(),
                subscription.getEndDate(),
                subscription.getIsActive(),
                subscription.getCreatedAt(),
                subscription.getUpdatedAt()
        );
    }

    public static Subscription toSubscriptionEntity(Business business, PricingPlan pricingPlan) {
        if (business == null || pricingPlan == null) {
            return null;
        }
        Subscription subscription = new Subscription();
        subscription.setBusiness(business);
        subscription.setPricingPlan(pricingPlan);
        subscription.setStartDate(java.time.LocalDateTime.now());
        subscription.setIsActive(true);
        return subscription;
    }

    public static BusinessSettingsResponse toBusinessSettingsResponse(BusinessSettings settings) {
        if (settings == null) {
            return null;
        }
        return new BusinessSettingsResponse(
                settings.getId(),
                settings.getBusiness().getId(),
                settings.getSettings(),
                settings.getCreatedAt(),
                settings.getUpdatedAt()
        );
    }

    public static BusinessSettings toBusinessSettingsEntity(Business business, BusinessSettingsRequest request) {
        if (business == null || request == null) {
            return null;
        }
        BusinessSettings settings = new BusinessSettings();
        settings.setBusiness(business);
        settings.setSettings(request.settings());
        return settings;
    }

    public static BusinessUserRoleResponse toBusinessUserRoleResponse(BusinessUserRole userRole) {
        if (userRole == null) {
            return null;
        }
        User user = userRole.getUser();
        return new BusinessUserRoleResponse(
                userRole.getId(),
                userRole.getBusiness().getId(),
                user.getId(),
                user.getEmail(),
                user.getFirstName() + " " + user.getLastName(),
                userRole.getRole(),
                userRole.getIsActive(),
                userRole.getCreatedAt(),
                userRole.getUpdatedAt()
        );
    }

    public static BusinessUserRole toBusinessUserRoleEntity(Business business, User user, String role) {
        if (business == null || user == null || role == null) {
            return null;
        }
        BusinessUserRole userRole = new BusinessUserRole();
        userRole.setBusiness(business);
        userRole.setUser(user);
        userRole.setRole(role);
        userRole.setIsActive(true);
        return userRole;
    }
}