package com.sky.tenant.entity;

import com.sky.core.entity.BaseEntity;
import jakarta.persistence.*;
import lombok.Data;
import lombok.EqualsAndHashCode;
import com.sky.tenant.enums.PlanType;

import java.math.BigDecimal;

@Data
@EqualsAndHashCode(callSuper = true)
@Entity
@Table(name = "pricing_plans", schema = "tenant")
public class PricingPlan extends BaseEntity {

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, unique = true)
    private PlanType planType;  // FREE, BASIC, PREMIUM, ENTERPRISE

    @Column(nullable = false)
    private String displayName;  // "Basic Plan", "Premium Plan"

    @Column
    private String description;  // Plan description

    @Column(nullable = false)
    private BigDecimal monthlyPrice;  // Price per month

    @Column(nullable = false)
    private Integer maxUsers;  // Max users allowed

    @Column(nullable = false)
    private Integer maxStorageGb;  // Max storage in GB

    @Column(columnDefinition = "jsonb")
    private String features;  // Features included: {appointments, inventory, loyalty, etc.}

    @Column(nullable = false)
    private Boolean isActive = true;
}
