package com.sky.tenant.service;

import com.sky.tenant.dto.RoleRequest;
import com.sky.tenant.dto.RoleResponse;
import com.sky.tenant.entity.Business;
import com.sky.tenant.entity.Permission;
import com.sky.tenant.entity.Role;
import com.sky.tenant.entity.RolePermission;
import com.sky.tenant.enums.PermissionType;
import com.sky.tenant.exception.BusinessNotFoundException;
import com.sky.tenant.exception.InvalidOperationException;
import com.sky.tenant.mapper.EntityMapper;
import com.sky.tenant.repository.BusinessRepository;
import com.sky.tenant.repository.PermissionRepository;
import com.sky.tenant.repository.RolePermissionRepository;
import com.sky.tenant.repository.RoleRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@Transactional
@RequiredArgsConstructor
public class RoleService {

    private final RoleRepository roleRepository;
    private final BusinessRepository businessRepository;
    private final PermissionRepository permissionRepository;
    private final RolePermissionRepository rolePermissionRepository;

    public RoleResponse createRole(UUID businessId, RoleRequest request) {
        Business business = businessRepository.findById(businessId)
                .orElseThrow(() -> new BusinessNotFoundException("Business not found"));

        if (roleRepository.findByBusinessIdAndName(businessId, request.name()).isPresent()) {
            throw new InvalidOperationException("Role with name '" + request.name() + "' already exists");
        }

        Role role = new Role();
        role.setBusiness(business);
        role.setName(request.name());
        role.setDescription(request.description());
        role.setIsSystemRole(false);
        role.setIsActive(true);

        
        Role savedRole = roleRepository.save(role);
        
        assignPermissions(savedRole, request.permissions());
        
        return EntityMapper.toRoleResponse(savedRole, request.permissions());
    }

    public RoleResponse updateRole(UUID businessId, UUID roleId, RoleRequest request) {
        Role role = roleRepository.findById(roleId)
                .filter(r -> r.getBusiness().getId().equals(businessId))
                .orElseThrow(() -> new InvalidOperationException("Role not found"));

        if (role.getIsSystemRole()) {
            throw new SecurityException("Cannot modify system roles");
        }

        role.setName(request.name());
        role.setDescription(request.description());

        
        Role savedRole = roleRepository.save(role);
        
        rolePermissionRepository.deleteByRoleId(role.getId());
        assignPermissions(savedRole, request.permissions());
        
        return EntityMapper.toRoleResponse(savedRole, request.permissions());
    }

    public void toggleRoleStatus(UUID businessId, UUID roleId, boolean isActive) {
        Role role = roleRepository.findById(roleId)
                .filter(r -> r.getBusiness().getId().equals(businessId))
                .orElseThrow(() -> new InvalidOperationException("Role not found"));

        if (role.getIsSystemRole() && !isActive) {
            throw new SecurityException("Cannot deactivate fundamental system roles");
        }

        role.setIsActive(isActive);

        roleRepository.save(role);
    }

    public void deleteRole(UUID businessId, UUID roleId) {
        Role role = roleRepository.findById(roleId)
                .filter(r -> r.getBusiness().getId().equals(businessId))
                .orElseThrow(() -> new InvalidOperationException("Role not found"));

        if (role.getIsSystemRole()) {
            throw new SecurityException("Cannot delete fundamental system roles");
        }

        roleRepository.delete(role);
    }

    @Transactional(readOnly = true)
    public Page<RoleResponse> getRolesForBusiness(UUID businessId, Pageable pageable) {
        return roleRepository.findByBusinessId(businessId, pageable)
                .map(role -> {
                    Set<PermissionType> permissions = rolePermissionRepository.findByRoleId(role.getId())
                            .stream()
                            .map(rp -> rp.getPermission().getName())
                            .collect(Collectors.toSet());
                    return EntityMapper.toRoleResponse(role, permissions);
                });
    }

    private void assignPermissions(Role role, Set<PermissionType> permissionTypes) {
        if (permissionTypes == null || permissionTypes.isEmpty()) return;

        for (PermissionType type : permissionTypes) {
            Permission permission = permissionRepository.findByName(type)
                    .orElseThrow(() -> new InvalidOperationException("Permission '" + type + "' not found"));
            
            RolePermission rolePermission = new RolePermission();
            rolePermission.setRole(role);
            rolePermission.setPermission(permission);
            rolePermission.setGranted(true);

            
            rolePermissionRepository.save(rolePermission);
        }
    }
}
