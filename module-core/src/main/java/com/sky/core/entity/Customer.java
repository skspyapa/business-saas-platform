package com.sky.core.entity;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
@Entity
@Table(name = "customers", schema = "core")
public class Customer extends BaseEntity {

    @Column(nullable = false)
    private UUID businessId;

    @Column
    private UUID userId; // References tenant.users (optional)

    @Column(nullable = false)
    private String firstName;

    @Column(nullable = false)
    private String lastName;

    @Column
    private String email;

    @Column
    private String phone;

    @Column
    private String address;

    @Column(nullable = false)
    private Integer loyaltyPoints = 0;

    @Column(nullable = false)
    private BigDecimal totalSpent = BigDecimal.ZERO;

    @Column
    private LocalDateTime lastPurchase;
}
