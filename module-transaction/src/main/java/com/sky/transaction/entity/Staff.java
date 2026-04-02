package com.sky.transaction.entity;

import com.sky.core.entity.BaseEntity;
import com.sky.tenant.entity.User;
import jakarta.persistence.*;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.UUID;

@Data
@EqualsAndHashCode(callSuper = true)
@Entity
@Table(name = "staff", schema = "transaction")
public class Staff extends BaseEntity {

    @Column(nullable = false)
    private UUID businessId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;

    @Column(nullable = false)
    private String firstName;

    @Column(nullable = false)
    private String lastName;

    @Column
    private String email;

    @Column
    private String phoneNumber;

    @Column
    private String title;

    @Column
    private String bio;

    @Column
    private String avatarUrl;

    @Column(nullable = false)
    private Boolean isActive = true;

    @Column(nullable = false)
    private Boolean isAvailable = true;
}
