package com.sky.transaction.entity;

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
@Table(name = "payments", schema = "transaction")
public class Payment extends BaseEntity {

    @Column(nullable = false)
    private UUID businessId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "order_id", nullable = false)
    private Order order;

    @Column(nullable = false)
    private UUID customerId;

    @Column(nullable = false)
    private BigDecimal amount;

    @Column
    private String currency = "USD";

    @Column(nullable = false)
    private String paymentMethod;

    @Column
    private String paymentGateway;

    @Column
    private String transactionId;

    @Column(nullable = false)
    private String status = "PENDING";

    @Column
    private String receiptUrl;

    @Column
    private String failureReason;
}
