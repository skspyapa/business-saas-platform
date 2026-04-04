package com.sky.tenant.service;

import com.sky.tenant.dto.BusinessSettingsRequest;
import com.sky.tenant.dto.BusinessSettingsResponse;
import com.sky.tenant.entity.Business;
import com.sky.tenant.entity.BusinessSettings;
import com.sky.tenant.exception.BusinessNotFoundException;
import com.sky.tenant.exception.InvalidOperationException;
import com.sky.tenant.mapper.EntityMapper;
import com.sky.tenant.repository.BusinessRepository;
import com.sky.tenant.repository.BusinessSettingsRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
@Transactional
public class BusinessSettingsService {

    private final BusinessSettingsRepository businessSettingsRepository;
    private final BusinessRepository businessRepository;

    public BusinessSettingsService(BusinessSettingsRepository businessSettingsRepository,
                                   BusinessRepository businessRepository) {
        this.businessSettingsRepository = businessSettingsRepository;
        this.businessRepository = businessRepository;
    }

    @Transactional(readOnly = true)
    public BusinessSettingsResponse getBusinessSettings(UUID businessId) {
        // Verify business exists
        Business business = businessRepository.findById(businessId)
                .orElseThrow(() -> new BusinessNotFoundException("Business with ID '" + businessId + "' not found"));

        // Get or create default settings
        BusinessSettings settings = businessSettingsRepository.findByBusinessId(businessId)
                .orElseGet(() -> {
                    BusinessSettings defaultSettings = new BusinessSettings();
                    defaultSettings.setBusiness(business);
                    defaultSettings.setTimezone("UTC");
                    defaultSettings.setCurrency("USD");
                    defaultSettings.setSettings("{}");
                    defaultSettings.setDescription("Default business settings");
                    defaultSettings.setCreatedBy("SYSTEM_PLACEHOLDER");
                    return businessSettingsRepository.save(defaultSettings);
                });

        return EntityMapper.toBusinessSettingsResponse(settings);
    }

    public BusinessSettingsResponse updateBusinessSettings(UUID businessId, BusinessSettingsRequest request) {
        if (request == null) {
            throw new InvalidOperationException("Settings request cannot be null");
        }

        Business business = businessRepository.findById(businessId)
                .orElseThrow(() -> new BusinessNotFoundException("Business with ID '" + businessId + "' not found"));

        BusinessSettings settings = businessSettingsRepository.findByBusinessId(businessId)
                .orElseThrow(() -> new InvalidOperationException("Settings not found for business with ID '" + businessId + "'"));

        // Update settings
        if (request.timezone() != null && !request.timezone().isBlank()) {
            settings.setTimezone(request.timezone());
        }
        if (request.currency() != null && !request.currency().isBlank()) {
            settings.setCurrency(request.currency());
        }
        if (request.settings() != null) {
            settings.setSettings(request.settings());
        }
        settings.setUpdatedBy("SYSTEM_PLACEHOLDER");

        BusinessSettings updated = businessSettingsRepository.save(settings);
        return EntityMapper.toBusinessSettingsResponse(updated);
    }

    public BusinessSettingsResponse updateTimezoneAndCurrency(UUID businessId, String timezone, String currency) {
        Business business = businessRepository.findById(businessId)
                .orElseThrow(() -> new BusinessNotFoundException("Business with ID '" + businessId + "' not found"));

        BusinessSettings settings = businessSettingsRepository.findByBusinessId(businessId)
                .orElseThrow(() -> new InvalidOperationException("Settings not found for business with ID '" + businessId + "'"));

        if (timezone != null && !timezone.isBlank()) {
            settings.setTimezone(timezone);
        }
        if (currency != null && !currency.isBlank()) {
            settings.setCurrency(currency);
        }
        settings.setUpdatedBy("SYSTEM_PLACEHOLDER");

        BusinessSettings updated = businessSettingsRepository.save(settings);
        return EntityMapper.toBusinessSettingsResponse(updated);
    }

    @Transactional(readOnly = true)
    public BusinessSettings getSettingsEntity(UUID businessId) {
        return businessSettingsRepository.findByBusinessId(businessId)
                .orElseThrow(() -> new InvalidOperationException("Settings not found for business with ID '" + businessId + "'"));
    }
}