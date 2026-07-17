package com.example.security.converter;

import com.example.security.authority.CapabilityGrantedAuthority;
import com.example.security.client.AccessControlManagerClient;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.convert.converter.Converter;
import org.springframework.security.authentication.AuthenticationServiceException;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;

import java.util.Collection;
import java.util.Collections;
import java.util.UUID;

/**
 * Converts a validated {@link Jwt} into a {@link JwtAuthenticationToken} whose
 * authorities are the RBAC capability codes fetched from Access Control Manager.
 *
 * <p>Registered as the {@code jwtAuthenticationConverter} in the default
 * {@code SecurityFilterChain}. Consuming applications that define their own
 * {@code SecurityFilterChain} must wire this converter manually:</p>
 *
 * <pre>{@code
 * http.oauth2ResourceServer(oauth2 -> oauth2
 *     .jwt(jwt -> jwt.jwtAuthenticationConverter(accessControlAuthenticationConverter)));
 * }</pre>
 */
@Slf4j
public class AccessControlAuthenticationConverter implements Converter<Jwt, JwtAuthenticationToken> {

    private final AccessControlManagerClient client;
    private final String systemUserIdClaim;
    private final boolean failOnError;

    public AccessControlAuthenticationConverter(
            AccessControlManagerClient client,
            String systemUserIdClaim,
            boolean failOnError) {
        this.client = client;
        this.systemUserIdClaim = systemUserIdClaim;
        this.failOnError = failOnError;
    }

    @Override
    public JwtAuthenticationToken convert(Jwt jwt) {
        UUID systemUserId = extractSystemUserId(jwt);

        Collection<CapabilityGrantedAuthority> authorities;
        try {
            authorities = client.getCapabilities(systemUserId).stream()
                    .map(CapabilityGrantedAuthority::new)
                    .toList();
        } catch (Exception ex) {
            log.error("Failed to fetch capabilities from ACM for user {}: {}", systemUserId, ex.getMessage());
            if (failOnError) {
                throw new AuthenticationServiceException(
                        "Could not load user capabilities from Access Control Manager", ex);
            }
            log.warn("Continuing with empty capability set for user {} (fail-on-error=false)", systemUserId);
            authorities = Collections.emptyList();
        }

        return new JwtAuthenticationToken(jwt, authorities, systemUserId.toString());
    }

    private UUID extractSystemUserId(Jwt jwt) {
        String raw = jwt.getClaimAsString(systemUserIdClaim);
        if (raw == null || raw.isBlank()) {
            throw new IllegalArgumentException(
                    "JWT is missing required claim '" + systemUserIdClaim + "'");
        }
        try {
            return UUID.fromString(raw);
        } catch (IllegalArgumentException ex) {
            throw new IllegalArgumentException(
                    "JWT claim '" + systemUserIdClaim + "' is not a valid UUID: " + raw, ex);
        }
    }
}
