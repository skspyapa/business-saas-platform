package com.sky.transaction.entity;

import java.math.BigDecimal;
import java.util.UUID;

import com.sky.core.entity.BaseEntity;
import com.sky.core.entity.Customer;
import jakarta.persistence.*;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
@Entity
@Table(name = "orders", schema = "transaction")
public class Order extends BaseEntity {

    @Column(nullable = false)
    private UUID businessId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "customer_id", nullable = false)
    private Customer customer;

    @Column(nullable = false, unique = true)
    private String orderNumber;

    @Column(nullable = false)
    private String orderType;

    @Column(nullable = false)
    private String status = "PENDING";

    @Column(nullable = false)
    private BigDecimal subtotal = BigDecimal.ZERO;

    @Column(nullable = false)
    private BigDecimal taxAmount = BigDecimal.ZERO;

    @Column(nullable = false)
    private BigDecimal discountAmount = BigDecimal.ZERO;

    @Column(nullable = false)
    private BigDecimal totalAmount = BigDecimal.ZERO;

    @Column
    private String currency = "USD";

    @Column
    private String paymentMethod;

    @Column
    private String paymentStatus;

    @Column
    private String shippingAddress;

    @Column
    private String notes;
}