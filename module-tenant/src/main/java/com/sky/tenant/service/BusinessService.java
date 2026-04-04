package com.sky.tenant.service;

import com.sky.tenant.dto.BusinessRequest;
import com.sky.tenant.dto.BusinessResponse;
import com.sky.tenant.entity.Business;
import com.sky.tenant.entity.BusinessSettings;
import com.sky.tenant.entity.PricingPlan;
import com.sky.tenant.entity.User;
import com.sky.tenant.exception.BusinessNotFoundException;
import com.sky.tenant.exception.DuplicateBusinessException;
import com.sky.tenant.exception.InvalidOperationException;
import com.sky.tenant.mapper.EntityMapper;
import com.sky.tenant.repository.BusinessRepository;
import com.sky.tenant.repository.BusinessSettingsRepository;
import com.sky.tenant.repository.PricingPlanRepository;
import com.sky.tenant.repository.UserRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
@Transactional
public class BusinessService {

    private final BusinessRepository businessRepository;
    private final UserRepository userRepository;
    private final BusinessSettingsRepository businessSettingsRepository;
    private final PricingPlanRepository pricingPlanRepository;
    private final SubscriptionService subscriptionService;

    public BusinessService(BusinessRepository businessRepository, UserRepository userRepository,
                          BusinessSettingsRepository businessSettingsRepository,
                          PricingPlanRepository pricingPlanRepository,
                          SubscriptionService subscriptionService) {
        this.businessRepository = businessRepository;
        this.userRepository = userRepository;
        this.businessSettingsRepository = businessSettingsRepository;
        this.pricingPlanRepository = pricingPlanRepository;
        this.subscriptionService = subscriptionService;
    }

    public BusinessResponse createBusiness(BusinessRequest request, UUID ownerId) {
        if (request == null) {
            throw new InvalidOperationException("Business request cannot be null");
        }

        // Check if subdomain already exists
        if (businessRepository.findBySubdomain(request.subdomain()).isPresent()) {
            throw new DuplicateBusinessException("Subdomain '" + request.subdomain() + "' is already in use");
        }

        // Fetch owner
        User owner = userRepository.findById(ownerId)
                .orElseThrow(() -> new BusinessNotFoundException("Owner with ID '" + ownerId + "' not found"));

        // Create business entity
        Business business = EntityMapper.toBusinessEntity(request, owner);
        business.setCreatedBy("SYSTEM_PLACEHOLDER"); // Placeholder - will be replaced with actual user from SecurityContext
        Business savedBusiness = businessRepository.save(business);

        // Auto-create default business settings
        BusinessSettings settings = new BusinessSettings();
        settings.setBusiness(savedBusiness);
        settings.setTimezone("UTC");
        settings.setCurrency("USD");
        settings.setSettings("{}");
        settings.setDescription("Default settings");
        settings.setCreatedBy("SYSTEM_PLACEHOLDER");
        businessSettingsRepository.save(settings);

        // Auto-assign FREE pricing plan subscription
        PricingPlan freePlan = pricingPlanRepository.findByPlanType("FREE")
                .orElseThrow(() -> new InvalidOperationException("FREE pricing plan not found in system"));
        subscriptionService.assignSubscription(savedBusiness.getId(), freePlan.getId());

        return EntityMapper.toBusinessResponse(savedBusiness);
    }

    public BusinessResponse updateBusiness(UUID businessId, BusinessRequest request) {
        if (request == null) {
            throw new InvalidOperationException("Business request cannot be null");
        }

        Business business = businessRepository.findById(businessId)
                .orElseThrow(() -> new BusinessNotFoundException("Business with ID '" + businessId + "' not found"));

        // Check if new subdomain is unique (if changed)
        if (!business.getSubdomain().equals(request.subdomain()) &&
                businessRepository.findBySubdomain(request.subdomain()).isPresent()) {
            throw new DuplicateBusinessException("Subdomain '" + request.subdomain() + "' is already in use");
        }

        // Update fields
        business.setName(request.name());
        business.setBusinessType(request.businessType());
        business.setDescription(request.description());
        business.setSubdomain(request.subdomain());
        business.setLogoUrl(request.logoUrl());
        business.setWebsiteUrl(request.websiteUrl());
        business.setCountry(request.country());
        business.setCity(request.city());
        business.setAddress(request.address());
        business.setUpdatedBy("SYSTEM_PLACEHOLDER");

        Business updatedBusiness = businessRepository.save(business);
        return EntityMapper.toBusinessResponse(updatedBusiness);
    }

    @Transactional(readOnly = true)
    public BusinessResponse getBusinessById(UUID businessId) {
        Business business = businessRepository.findById(businessId)
                .orElseThrow(() -> new BusinessNotFoundException("Business with ID '" + businessId + "' not found"));
        return EntityMapper.toBusinessResponse(business);
    }

    @Transactional(readOnly = true)
    public Page<BusinessResponse> getAllBusinesses(Pageable pageable, String searchName) {
        Page<Business> page;
        if (searchName != null && !searchName.isBlank()) {
            page = businessRepository.findByNameContainingIgnoreCaseAndIsActiveTrue(searchName, pageable);
        } else {
            page = businessRepository.findByIsActiveTrue(pageable);
        }
        return page.map(EntityMapper::toBusinessResponse);
    }

    @Transactional(readOnly = true)
    public Page<BusinessResponse> getBusinessesByOwner(UUID ownerId, Pageable pageable) {
        Page<Business> page = businessRepository.findByOwnerId(ownerId, pageable);
        return page.map(EntityMapper::toBusinessResponse);
    }

    public BusinessResponse deactivateBusiness(UUID businessId) {
        Business business = businessRepository.findById(businessId)
                .orElseThrow(() -> new BusinessNotFoundException("Business with ID '" + businessId + "' not found"));

        if (!business.getIsActive()) {
            throw new InvalidOperationException("Business is already deactivated");
        }

        business.setIsActive(false);
        business.setUpdatedBy("SYSTEM_PLACEHOLDER");
        Business deactivated = businessRepository.save(business);
        return EntityMapper.toBusinessResponse(deactivated);
    }

    public BusinessResponse activateBusiness(UUID businessId) {
        Business business = businessRepository.findById(businessId)
                .orElseThrow(() -> new BusinessNotFoundException("Business with ID '" + businessId + "' not found"));

        if (business.getIsActive()) {
            throw new InvalidOperationException("Business is already active");
        }

        business.setIsActive(true);
        business.setUpdatedBy("SYSTEM_PLACEHOLDER");
        Business activated = businessRepository.save(business);
        return EntityMapper.toBusinessResponse(activated);
    }

    @Transactional(readOnly = true)
    public Business getBusinessEntityById(UUID businessId) {
        return businessRepository.findById(businessId)
                .orElseThrow(() -> new BusinessNotFoundException("Business with ID '" + businessId + "' not found"));
    }
}