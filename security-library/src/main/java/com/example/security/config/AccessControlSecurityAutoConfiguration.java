package com.example.security.config;

import com.example.security.annotation.RequiresCapability;
import com.example.security.authorization.CapabilityAuthorizationManager;
import com.example.security.client.AccessControlManagerClient;
import com.example.security.converter.AccessControlAuthenticationConverter;
import lombok.extern.slf4j.Slf4j;
import org.aopalliance.intercept.MethodInvocation;
import org.springframework.aop.support.StaticMethodMatcherPointcut;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Role;
import org.springframework.core.annotation.AnnotationUtils;
import org.springframework.security.authorization.method.AuthorizationManagerBeforeMethodInterceptor;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.web.client.RestClient;

import java.lang.reflect.Method;

/**
 * Spring Boot auto-configuration for RBAC via Access Control Manager.
 *
 * <p>Activates when:</p>
 * <ul>
 *   <li>{@code SecurityFilterChain} is on the classpath (Spring Security present)</li>
 *   <li>{@code access-control.security.manager-url} is configured</li>
 * </ul>
 *
 * <p>Registers:</p>
 * <ul>
 *   <li>{@link AccessControlManagerClient} — calls ACM to fetch capabilities</li>
 *   <li>{@link AccessControlAuthenticationConverter} — converts JWTs into authenticated
 *       principals with capability-based {@code GrantedAuthority} objects</li>
 *   <li>{@link CapabilityAuthorizationManager} — enforces {@code @RequiresCapability}</li>
 *   <li>A default {@link SecurityFilterChain} (only if the consuming app does not define one)</li>
 * </ul>
 */
@Slf4j
@AutoConfiguration
@EnableConfigurationProperties(AccessControlSecurityProperties.class)
@ConditionalOnClass({SecurityFilterChain.class, Jwt.class})
@ConditionalOnProperty(prefix = "access-control.security", name = "manager-url")
public class AccessControlSecurityAutoConfiguration {

    @Bean
    @ConditionalOnMissingBean
    public AccessControlManagerClient accessControlManagerClient(
            RestClient.Builder builder,
            AccessControlSecurityProperties properties) {
        RestClient restClient = builder.baseUrl(properties.getManagerUrl()).build();
        log.info("Registered AccessControlManagerClient → {}", properties.getManagerUrl());
        return new AccessControlManagerClient(restClient);
    }

    @Bean
    @ConditionalOnMissingBean
    public AccessControlAuthenticationConverter accessControlAuthenticationConverter(
            AccessControlManagerClient client,
            AccessControlSecurityProperties properties) {
        return new AccessControlAuthenticationConverter(
                client,
                properties.getSystemUserIdClaim(),
                properties.isFailOnError());
    }

    @Bean
    @ConditionalOnMissingBean
    public CapabilityAuthorizationManager capabilityAuthorizationManager() {
        return new CapabilityAuthorizationManager();
    }

    /**
     * Registers the AOP advisor that enforces {@code @RequiresCapability} on
     * methods and classes. Runs before Spring Security's own {@code @PreAuthorize}
     * interceptor so capability checks fail fast.
     */
    @Bean
    @Role(BeanDefinition.ROLE_INFRASTRUCTURE)
    public AuthorizationManagerBeforeMethodInterceptor requiresCapabilityInterceptor(
            CapabilityAuthorizationManager manager) {
        var pointcut = new StaticMethodMatcherPointcut() {
            @Override
            public boolean matches(Method method, Class<?> targetClass) {
                return AnnotationUtils.findAnnotation(method, RequiresCapability.class) != null
                        || AnnotationUtils.findAnnotation(targetClass, RequiresCapability.class) != null;
            }
        };
        var interceptor = new AuthorizationManagerBeforeMethodInterceptor(pointcut, manager);
        interceptor.setOrder(org.springframework.security.authorization.method
                .AuthorizationInterceptorsOrder.PRE_AUTHORIZE.getOrder() - 1);
        return interceptor;
    }

    /**
     * Default {@link SecurityFilterChain}: stateless JWT, all requests authenticated.
     * Consuming applications that need custom security rules should define their own
     * {@code SecurityFilterChain} bean — this default will be skipped.
     */
    @Bean
    @ConditionalOnMissingBean(SecurityFilterChain.class)
    public SecurityFilterChain defaultSecurityFilterChain(
            HttpSecurity http,
            AccessControlAuthenticationConverter converter) throws Exception {
        return http
                .authorizeHttpRequests(auth -> auth.anyRequest().authenticated())
                .oauth2ResourceServer(oauth2 -> oauth2
                        .jwt(jwt -> jwt.jwtAuthenticationConverter(converter)))
                .sessionManagement(session -> session
                        .sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .csrf(csrf -> csrf.disable())
                .build();
    }
}
