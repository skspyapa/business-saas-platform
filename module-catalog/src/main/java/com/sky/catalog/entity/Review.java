package com.sky.catalog.entity;

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
@Table(name = "reviews", schema = "catalog")
public class Review extends BaseEntity {

    @Column(nullable = false)
    private UUID businessId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "product_id")
    private Product product;

    @Column(nullable = false)
    private UUID customerId;

    @Column
    private UUID staffId;

    @Column(nullable = false)
    private Integer rating;

    @Column
    private String title;

    @Column
    private String comment;

    @Column(nullable = false)
    private Boolean isVerifiedPurchase = false;

    @Column(nullable = false)
    private Integer helpfulCount = 0;

    @Column(nullable = false)
    private String status = "PENDING";
}
