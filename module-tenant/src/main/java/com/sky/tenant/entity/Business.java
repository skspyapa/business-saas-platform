package com.sky.tenant.entity;

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
@Table(name = "businesses", schema = "tenant")
public class Business extends BaseEntity {

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "owner_id", nullable = false)
    private User owner;

    @Column(nullable = false)
    private String name;

    @Column(nullable = false)
    private String businessType;

    @Column
    private String description;

    @Column(nullable = false, unique = true)
    private String subdomain;

    @Column
    private String logoUrl;

    @Column
    private String websiteUrl;

    @Column
    private String country;

    @Column
    private String city;

    @Column
    private String address;

    @Column(nullable = false)
    private Boolean isActive = true;
}