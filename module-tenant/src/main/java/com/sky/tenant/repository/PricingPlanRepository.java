package com.sky.tenant.repository;

import com.sky.tenant.entity.PricingPlan;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface PricingPlanRepository extends JpaRepository<PricingPlan, UUID> {
    Optional<PricingPlan> findByPlanType(String planType);
    Optional<PricingPlan> findByPlanTypeAndIsActiveTrue(String planType);
}