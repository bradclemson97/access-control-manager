package com.example.security.config;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.validation.annotation.Validated;

/**
 * Configuration properties for the security library.
 *
 * <p>Minimal required configuration in the consuming application:</p>
 * <pre>{@code
 * access-control:
 *   security:
 *     manager-url: http://access-control-manager:8130
 * }</pre>
 */
@Getter
@Setter
@Validated
@ConfigurationProperties(prefix = "access-control.security")
public class AccessControlSecurityProperties {

    /**
     * Base URL of the Access Control Manager service.
     * Required — the auto-configuration is inactive without this property.
     */
    @NotBlank
    private String managerUrl;

    /**
     * Name of the JWT claim that contains the user's {@code systemUserId} (UUID).
     * Defaults to {@code "sub"} which is the standard JWT subject claim.
     */
    private String systemUserIdClaim = "sub";

    /**
     * When {@code true}, authentication fails if the ACM call throws an exception.
     * When {@code false} (default), authentication succeeds with an empty capability set —
     * method-level checks will block access but the request is not rejected outright.
     */
    private boolean failOnError = false;
}
