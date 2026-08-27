package com.example.accesscontrolmanager.config;

import com.example.security.converter.AccessControlAuthenticationConverter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
public class SecurityConfig {

    /**
     * Chain 1: handles the capabilities endpoint with plain JWT — no capability lookup.
     * This breaks the circular dependency where the converter would call /capabilities
     * to authenticate a /capabilities request.
     */
    @Bean
    @Order(1)
    public SecurityFilterChain capabilitiesFilterChain(HttpSecurity http) throws Exception {
        return http
                .securityMatcher("/v1/user/*/capabilities")
                .authorizeHttpRequests(auth -> auth.anyRequest().authenticated())
                .oauth2ResourceServer(oauth2 -> oauth2.jwt(jwt -> {}))
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .csrf(AbstractHttpConfigurer::disable)
                .build();
    }

    /**
     * Chain 2: all other endpoints use the full converter which populates capabilities
     * as GrantedAuthority objects for @RequiresCapability checks.
     */
    @Bean
    @Order(2)
    public SecurityFilterChain mainFilterChain(HttpSecurity http, AccessControlAuthenticationConverter converter) throws Exception {
        return http
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers("/actuator/health", "/v1/docs", "/v1/docs/**", "/v1/api-docs", "/v1/api-docs/**", "/swagger-ui/**").permitAll()
                        .anyRequest().authenticated())
                .oauth2ResourceServer(oauth2 -> oauth2.jwt(jwt -> jwt.jwtAuthenticationConverter(converter)))
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .csrf(AbstractHttpConfigurer::disable)
                .build();
    }
}
