package com.sky.tenant.entity;

import com.sky.core.entity.BaseEntity;
import jakarta.persistence.*;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.time.LocalDateTime;

@Data
@EqualsAndHashCode(callSuper = true)
@Entity
@Table(name = "subscriptions", schema = "tenant")
public class Subscription extends BaseEntity {

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "business_id", nullable = false)
    private Business business;

    @Column(nullable = false)
    private String planName;

    @Column(nullable = false)
    private String planType; // FREE, BASIC, PREMIUM, ENTERPRISE

    @Column(nullable = false)
    private LocalDateTime startDate;

    @Column
    private LocalDateTime endDate;

    @Column(nullable = false)
    private Boolean isActive = true;

    @Column
    private Integer maxUsers;

    @Column
    private Integer maxStorageGb;
}