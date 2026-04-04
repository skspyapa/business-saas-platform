package com.sky.tenant.repository;

import com.sky.tenant.entity.Subscription;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface SubscriptionRepository extends JpaRepository<Subscription, UUID> {
    Optional<Subscription> findByBusinessIdAndIsActiveTrue(UUID businessId);
    Optional<Subscription> findByBusinessId(UUID businessId);
}