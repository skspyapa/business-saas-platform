package com.sky.tenant.service;

import com.sky.tenant.dto.BusinessUserRoleResponse;
import com.sky.tenant.entity.Business;
import com.sky.tenant.entity.BusinessUserRole;
import com.sky.tenant.entity.User;
import com.sky.tenant.exception.BusinessNotFoundException;
import com.sky.tenant.exception.InvalidOperationException;
import com.sky.tenant.mapper.EntityMapper;
import com.sky.tenant.repository.BusinessRepository;
import com.sky.tenant.repository.BusinessUserRoleRepository;
import com.sky.tenant.repository.UserRepository;
import com.sky.tenant.repository.RoleRepository;
import com.sky.tenant.entity.Role;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
@Transactional
public class BusinessUserRoleService {

    private final BusinessUserRoleRepository businessUserRoleRepository;
    private final BusinessRepository businessRepository;
    private final UserRepository userRepository;
    private final RoleRepository roleRepository;

    public BusinessUserRoleService(BusinessUserRoleRepository businessUserRoleRepository,
                                   BusinessRepository businessRepository,
                                   UserRepository userRepository,
                                   RoleRepository roleRepository) {
        this.businessUserRoleRepository = businessUserRoleRepository;
        this.businessRepository = businessRepository;
        this.userRepository = userRepository;
        this.roleRepository = roleRepository;
    }

    public BusinessUserRoleResponse assignUserToBusinessWithRole(UUID businessId, UUID userId, String role) {
        if (role == null || role.isBlank()) {
            throw new InvalidOperationException("Role cannot be null or blank");
        }

        Business business = businessRepository.findById(businessId)
                .orElseThrow(() -> new BusinessNotFoundException("Business with ID '" + businessId + "' not found"));

        Role roleEntity = roleRepository.findByBusinessIdAndName(businessId, role)
                .orElseThrow(() -> new InvalidOperationException("Role '" + role + "' does not exist for this business."));

        if (!roleEntity.getIsActive()) {
            throw new InvalidOperationException("Cannot assign user to role '" + role + "' because it is deactivated.");
        }

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new InvalidOperationException("User with ID '" + userId + "' not found"));

        // Check if user already has a role in this business
        if (businessUserRoleRepository.findByBusinessIdAndUserId(businessId, userId).isPresent()) {
            throw new InvalidOperationException("User already has a role in this business. Use update endpoint to change role");
        }

        BusinessUserRole businessUserRole = new BusinessUserRole();
        businessUserRole.setBusiness(business);
        businessUserRole.setUser(user);
        businessUserRole.setRole(roleEntity);
        businessUserRole.setIsActive(true);


        BusinessUserRole saved = businessUserRoleRepository.save(businessUserRole);
        return EntityMapper.toBusinessUserRoleResponse(saved);
    }

    @Transactional(readOnly = true)
    public Page<BusinessUserRoleResponse> getUsersForBusiness(UUID businessId, Pageable pageable) {
        Business business = businessRepository.findById(businessId)
                .orElseThrow(() -> new BusinessNotFoundException("Business with ID '" + businessId + "' not found"));

        Page<BusinessUserRole> page = businessUserRoleRepository.findByBusinessIdAndIsActiveTrue(businessId, pageable);
        return page.map(EntityMapper::toBusinessUserRoleResponse);
    }

    public BusinessUserRoleResponse updateUserRole(UUID businessId, UUID userId, String newRole) {
        if (newRole == null || newRole.isBlank()) {
            throw new InvalidOperationException("New role cannot be null or blank");
        }

        // Verify business exists
        Business business = businessRepository.findById(businessId)
                .orElseThrow(() -> new BusinessNotFoundException("Business with ID '" + businessId + "' not found"));

        Role roleEntity = roleRepository.findByBusinessIdAndName(businessId, newRole)
                .orElseThrow(() -> new InvalidOperationException("Role '" + newRole + "' does not exist for this business."));

        if (!roleEntity.getIsActive()) {
            throw new InvalidOperationException("Cannot assign user to role '" + newRole + "' because it is deactivated.");
        }

        BusinessUserRole businessUserRole = businessUserRoleRepository.findByBusinessIdAndUserId(businessId, userId)
                .orElseThrow(() -> new InvalidOperationException("User role not found for business"));

        businessUserRole.setRole(roleEntity);


        BusinessUserRole updated = businessUserRoleRepository.save(businessUserRole);
        return EntityMapper.toBusinessUserRoleResponse(updated);
    }

    public void removeUserFromBusiness(UUID businessId, UUID userId) {
        // Verify business exists
        Business business = businessRepository.findById(businessId)
                .orElseThrow(() -> new BusinessNotFoundException("Business with ID '" + businessId + "' not found"));

        BusinessUserRole businessUserRole = businessUserRoleRepository.findByBusinessIdAndUserId(businessId, userId)
                .orElseThrow(() -> new InvalidOperationException("User role not found for business"));

        businessUserRole.setIsActive(false);

        businessUserRoleRepository.save(businessUserRole);
    }

    @Transactional(readOnly = true)
    public BusinessUserRoleResponse getUserRoleInBusiness(UUID businessId, UUID userId) {
        BusinessUserRole businessUserRole = businessUserRoleRepository.findByBusinessIdAndUserId(businessId, userId)
                .orElseThrow(() -> new InvalidOperationException("User role not found for business"));

        return EntityMapper.toBusinessUserRoleResponse(businessUserRole);
    }
}
