package com.example.security.client.dto;

import java.util.Set;

/**
 * Response DTO for the Access Control Manager capabilities endpoint.
 */
public record CapabilitiesResponse(Set<String> capabilities) {

    public CapabilitiesResponse {
        capabilities = capabilities != null ? capabilities : Set.of();
    }
}
