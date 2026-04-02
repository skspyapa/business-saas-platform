package com.sky.tenant.entity;

import com.sky.core.entity.BaseEntity;
import jakarta.persistence.*;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
@Entity
@Table(name = "business_user_roles", schema = "tenant")
public class BusinessUserRole extends BaseEntity {

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "business_id", nullable = false)
    private Business business;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Column(nullable = false)
    private String role; // OWNER, ADMIN, MANAGER, STAFF

    @Column(nullable = false)
    private Boolean isActive = true;

    @Column
    private String permissions; // JSON string of permissions
}