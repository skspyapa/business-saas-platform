package com.sky.core.config;

import org.springframework.data.domain.AuditorAware;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
public class AuditorAwareImpl implements AuditorAware<String> {

    @Override
    public Optional<String> getCurrentAuditor() {
        // TODO: Replace with actual user from Spring Security context
        // For now, return "system" as placeholder
        return Optional.of("system");
    }
}