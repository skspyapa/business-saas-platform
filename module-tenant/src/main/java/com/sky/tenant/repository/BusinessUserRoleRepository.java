package com.sky.tenant.repository;

import com.sky.tenant.entity.BusinessUserRole;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface BusinessUserRoleRepository extends JpaRepository<BusinessUserRole, UUID> {
    Optional<BusinessUserRole> findByBusinessIdAndUserId(UUID businessId, UUID userId);
    Page<BusinessUserRole> findByBusinessIdAndIsActiveTrue(UUID businessId, Pageable pageable);
    Page<BusinessUserRole> findByBusinessId(UUID businessId, Pageable pageable);
}