package com.sky.tenant.repository;

import com.sky.tenant.entity.Business;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface BusinessRepository extends JpaRepository<Business, UUID> {
    Optional<Business> findBySubdomain(String subdomain);
    Page<Business> findByOwnerId(UUID ownerId, Pageable pageable);
    Page<Business> findByNameContainingIgnoreCase(String name, Pageable pageable);
    Page<Business> findByIsActiveTrue(Pageable pageable);
    Page<Business> findByNameContainingIgnoreCaseAndIsActiveTrue(String name, Pageable pageable);
}