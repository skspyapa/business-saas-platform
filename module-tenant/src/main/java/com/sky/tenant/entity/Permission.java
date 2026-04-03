package com.sky.tenant.entity;

import com.sky.core.entity.BaseEntity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
@Entity
@Table(name = "permissions", schema = "tenant")
public class Permission extends BaseEntity {

    @Column(nullable = false, unique = true)
    private String name;  // can_edit_settings, can_manage_staff, can_process_refunds

    @Column
    private String description;  // Human-readable description

    @Column
    private String category;  // business_settings, staff_management, order_management, etc.
}
