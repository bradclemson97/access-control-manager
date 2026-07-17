package com.example.security.annotation;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Enforces that the authenticated user holds the named capability before the
 * annotated method or all methods on the annotated class are invoked.
 *
 * <p>Can be used directly or as a meta-annotation to define domain-specific
 * shorthand annotations:</p>
 *
 * <pre>{@code
 * @RequiresCapability("CREATE_USERS")
 * @Target({ElementType.METHOD, ElementType.TYPE})
 * @Retention(RetentionPolicy.RUNTIME)
 * public @interface CanCreateUsers {}
 * }</pre>
 *
 * <p>Capabilities are populated into the Spring {@code SecurityContext} by the
 * {@code AccessControlAuthenticationConverter} on every authenticated request.</p>
 */
@Target({ElementType.METHOD, ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface RequiresCapability {

    /**
     * The capability code that the authenticated user must hold.
     * This must match a role name of type {@code CAPABILITY} in Access Control Manager.
     */
    String value();
}
