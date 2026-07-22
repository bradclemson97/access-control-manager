package com.example.accesscontrolmanager.config;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.domain.AuditorAware;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

import java.util.Optional;
import java.util.UUID;

/**
 * Enables JPA auditing so that {@code createdBy}, {@code modifiedBy},
 * {@code createdDate}, and {@code modifiedDate} fields are populated automatically.
 *
 * <p>The auditor is resolved from the authenticated principal's {@code systemUserId}.
 * A configurable fallback UUID is used for unauthenticated or system-initiated writes.</p>
 */
@Configuration
@EnableJpaAuditing(auditorAwareRef = "auditorAware")
@Slf4j
public class JpaAuditingConfig {

    @Value("${system.user-id:00000000-0000-0000-0000-000000000000}")
    private UUID fallbackSystemUserId;

    @Bean
    public AuditorAware<UUID> auditorAware() {
        return () -> {
            Authentication auth = SecurityContextHolder.getContext().getAuthentication();
            if (auth == null || !auth.isAuthenticated()
                    || "anonymousUser".equals(auth.getPrincipal())) {
                return Optional.of(fallbackSystemUserId);
            }
            try {
                return Optional.of(UUID.fromString(auth.getName()));
            } catch (IllegalArgumentException e) {
                return Optional.of(fallbackSystemUserId);
            }
        };
    }
}
