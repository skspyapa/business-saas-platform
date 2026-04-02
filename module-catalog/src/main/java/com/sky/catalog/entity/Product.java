package com.sky.catalog.entity;

import java.math.BigDecimal;
import java.util.UUID;

import com.sky.core.entity.BaseEntity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
@Entity
@Table(name = "products", schema = "catalog")
public class Product extends BaseEntity {

    @Column(nullable = false)
    private UUID businessId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "category_id")
    private Category category;

    @Column(nullable = false)
    private String name;

    @Column
    private String description;

    @Column(nullable = false)
    private String productType; // PHYSICAL, DIGITAL, SERVICE

    @Column(nullable = false)
    private BigDecimal basePrice;

    @Column
    private BigDecimal costPrice;

    @Column
    private String currency = "USD";

    @Column
    private String images; // JSON array

    @Column(nullable = false)
    private Boolean isActive = true;

    @Column(nullable = false)
    private Boolean isAvailable = true;
}
