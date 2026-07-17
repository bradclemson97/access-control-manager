package com.example.security.authorization;

import com.example.security.annotation.RequiresCapability;
import org.aopalliance.intercept.MethodInvocation;
import org.springframework.core.annotation.AnnotationUtils;
import org.springframework.security.authorization.AuthorizationDecision;
import org.springframework.security.authorization.AuthorizationManager;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;

import java.lang.reflect.Method;
import java.util.function.Supplier;

/**
 * Spring Security {@link AuthorizationManager} that enforces {@link RequiresCapability}
 * on method invocations.
 *
 * <p>Resolution order: method annotation takes precedence over class annotation.
 * Both direct usage and meta-annotation usage (annotations composed from
 * {@code @RequiresCapability}) are supported via Spring's {@link AnnotationUtils}.</p>
 */
public class CapabilityAuthorizationManager implements AuthorizationManager<MethodInvocation> {

    @Override
    public AuthorizationDecision check(Supplier<Authentication> authSupplier, MethodInvocation invocation) {
        RequiresCapability annotation = findAnnotation(invocation);
        if (annotation == null) {
            return new AuthorizationDecision(true);
        }

        Authentication auth = authSupplier.get();
        if (auth == null || !auth.isAuthenticated()) {
            return new AuthorizationDecision(false);
        }

        String required = annotation.value();
        boolean granted = auth.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .anyMatch(required::equals);

        return new AuthorizationDecision(granted);
    }

    private RequiresCapability findAnnotation(MethodInvocation invocation) {
        Method method = invocation.getMethod();
        RequiresCapability found = AnnotationUtils.findAnnotation(method, RequiresCapability.class);
        if (found == null) {
            found = AnnotationUtils.findAnnotation(method.getDeclaringClass(), RequiresCapability.class);
        }
        return found;
    }
}
