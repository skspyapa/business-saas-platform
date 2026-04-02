package com.sky.tenant.entity;

import com.sky.core.entity.BaseEntity;
import jakarta.persistence.*;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
@Entity
@Table(name = "business_settings", schema = "tenant")
public class BusinessSettings extends BaseEntity {

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "business_id", nullable = false)
    private Business business;

    @Column(nullable = false)
    private String settingKey;

    @Column(nullable = false)
    private String settingValue;

    @Column
    private String settingType; // STRING, NUMBER, BOOLEAN, JSON

    @Column
    private String description;
}