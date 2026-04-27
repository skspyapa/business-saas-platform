package com.sky.tenant.service;

import com.sky.tenant.entity.Permission;
import com.sky.tenant.enums.PermissionType;
import com.sky.tenant.repository.PermissionRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
public class PermissionSyncService {

    private final PermissionRepository permissionRepository;

    @EventListener(ApplicationReadyEvent.class)
    @Transactional
    public void syncPermissions() {
        log.info("Starting SaaS Permission Synchronization...");
        int addedCount = 0;
        int updatedCount = 0;

        for (PermissionType type : PermissionType.values()) {
            Permission permission = permissionRepository.findByName(type)
                    .orElseGet(() -> {
                        Permission p = new Permission();
                        p.setName(type);
                        p.setCreatedBy("SYSTEM_SYNC");
                        p.setUpdatedBy("SYSTEM_SYNC");
                        p.setCreatedAt(java.time.LocalDateTime.now());
                        p.setUpdatedAt(java.time.LocalDateTime.now());
                        return p;
                    });

            boolean isNew = permission.getId() == null;
            boolean isModified = isNew || 
                    !type.getDescription().equals(permission.getDescription()) || 
                    !type.getCategory().equals(permission.getCategory());

            if (isModified) {
                permission.setDescription(type.getDescription());
                permission.setCategory(type.getCategory());
                if (!isNew) {
                    permission.setUpdatedAt(java.time.LocalDateTime.now());
                    permission.setUpdatedBy("SYSTEM_SYNC");
                }
                
                permissionRepository.save(permission);
                
                if (isNew) {
                    addedCount++;
                } else {
                    updatedCount++;
                }
            }
        }
        
        log.info("Permission Synchronization Complete. Added: {}, Updated: {}", addedCount, updatedCount);
    }
}
