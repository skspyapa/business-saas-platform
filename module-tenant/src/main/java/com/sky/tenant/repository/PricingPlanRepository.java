package com.sky.tenant.repository;

import com.sky.tenant.entity.PricingPlan;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;
import com.sky.tenant.enums.PlanType;

@Repository
public interface PricingPlanRepository extends JpaRepository<PricingPlan, UUID> {
    Optional<PricingPlan> findByPlanType(PlanType planType);
    Optional<PricingPlan> findByPlanTypeAndIsActiveTrue(PlanType planType);
}