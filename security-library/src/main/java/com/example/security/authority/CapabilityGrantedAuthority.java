package com.example.security.authority;

import org.springframework.security.core.GrantedAuthority;

/**
 * A {@link GrantedAuthority} representing a single RBAC capability code
 * fetched from Access Control Manager (e.g., {@code "CREATE_USERS"}).
 */
public record CapabilityGrantedAuthority(String authority) implements GrantedAuthority {

    @Override
    public String getAuthority() {
        return authority;
    }
}
