package com.sky.catalog.entity;

import java.util.UUID;

import com.sky.core.entity.BaseEntity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
@Entity
@Table(name = "categories", schema = "catalog")
public class Category extends BaseEntity {

    @Column(nullable = false)
    private UUID businessId;

    @Column(nullable = false)
    private String name;

    @Column
    private String description;

    @Column
    private String color;

    @Column(nullable = false)
    private Integer displayOrder = 0;

    @Column(nullable = false)
    private Boolean isActive = true;
}