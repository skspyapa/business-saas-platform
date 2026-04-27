package com.sky.tenant.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum PermissionType {
    
    // Business Management
    MANAGE_BUSINESS_SETTINGS("Update global business settings and branding", PermissionCategory.BUSINESS_MANAGEMENT),
    
    // User & Role Management
    MANAGE_ROLES("Create, modify, and delete custom roles", PermissionCategory.USER_MANAGEMENT),
    MANAGE_STAFF("Add, edit, and remove staff members", PermissionCategory.USER_MANAGEMENT),
    
    // Catalog Management
    MANAGE_PRODUCTS("Create and edit products and inventory variants", PermissionCategory.CATALOG),
    MANAGE_CATEGORIES("Manage product categories and collections", PermissionCategory.CATALOG),
    
    // Transaction Management
    PROCESS_ORDERS("Create, manage, and complete customer orders", PermissionCategory.TRANSACTIONS),
    PROCESS_REFUNDS("Issue financial refunds for completed payments", PermissionCategory.TRANSACTIONS),
    
    // Reporting
    VIEW_REPORTS("View financial, sales, and analytics reports", PermissionCategory.REPORTING);

    private final String description;
    private final PermissionCategory category;
}
