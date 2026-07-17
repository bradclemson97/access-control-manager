package com.example.security.client;

import com.example.security.client.dto.CapabilitiesResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.client.RestClient;

import java.util.Set;
import java.util.UUID;

/**
 * HTTP client for the Access Control Manager service.
 * Fetches the capability codes assigned (directly or via inheritance) to a user.
 */
@Slf4j
public class AccessControlManagerClient {

    private final RestClient restClient;

    public AccessControlManagerClient(RestClient restClient) {
        this.restClient = restClient;
    }

    /**
     * Returns the set of capability codes for the given user.
     * Returns an empty set if the ACM returns no body.
     */
    public Set<String> getCapabilities(UUID systemUserId) {
        log.debug("Fetching capabilities for user {}", systemUserId);
        CapabilitiesResponse response = restClient.get()
                .uri("/v1/user/{systemUserId}/capabilities", systemUserId)
                .retrieve()
                .body(CapabilitiesResponse.class);
        Set<String> capabilities = response != null ? response.capabilities() : Set.of();
        log.debug("Received {} capabilities for user {}", capabilities.size(), systemUserId);
        return capabilities;
    }
}
