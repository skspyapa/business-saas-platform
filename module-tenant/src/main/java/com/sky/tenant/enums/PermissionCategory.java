package com.sky.tenant.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum PermissionCategory {
    BUSINESS_MANAGEMENT("Business Management"),
    USER_MANAGEMENT("User Management"),
    CATALOG("Catalog"),
    TRANSACTIONS("Transactions"),
    REPORTING("Reporting");

    private final String displayName;
}
