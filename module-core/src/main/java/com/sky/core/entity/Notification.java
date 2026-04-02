package com.sky.core.entity;

import java.util.UUID;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
@Entity
@Table(name = "notifications", schema = "core")
public class Notification extends BaseEntity {

    @Column(nullable = false)
    private UUID businessId;

    @Column(nullable = false)
    private UUID recipientId;

    @Column(nullable = false)
    private String notificationType;

    @Column(nullable = false)
    private String title;

    @Column(nullable = false)
    private String message;

    @Column
    private String data; // JSON

    @Column(nullable = false)
    private Boolean isRead = false;
}
