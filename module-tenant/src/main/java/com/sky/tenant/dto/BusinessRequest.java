package com.sky.tenant.dto;

public record BusinessRequest(
        String name,
        String businessType,
        String description,
        String subdomain,
        String logoUrl,
        String websiteUrl,
        String country,
        String city,
        String address
) {
}