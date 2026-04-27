package com.sky.tenant.entity;

import com.sky.core.entity.BaseEntity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Table;
import com.sky.tenant.enums.PermissionCategory;
import com.sky.tenant.enums.PermissionType;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
@Entity
@Table(name = "permissions", schema = "tenant")
public class Permission extends BaseEntity {

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, unique = true)
    private PermissionType name;

    @Column
    private String description;  // Human-readable description

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private PermissionCategory category;  // mapped via enum for strict safety
}
