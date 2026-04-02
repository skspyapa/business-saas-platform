package com.sky.core.entity;

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
@Table(name = "inventory", schema = "core")
public class Inventory extends BaseEntity {

    @Column(nullable = false)
    private UUID businessId;

    @Column(nullable = false)
    private UUID productVariantId; // References catalog.product_variants

    @Column(nullable = false)
    private Integer quantityAvailable = 0;

    @Column(nullable = false)
    private Integer quantityReserved = 0;

    @Column(nullable = false)
    private Integer quantityDamaged = 0;

    @Column
    private LocalDateTime lastStockCheck;
}
