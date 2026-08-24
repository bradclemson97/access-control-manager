package com.example.accesscontrolmanager.client;

import lombok.extern.slf4j.Slf4j;
import org.springframework.web.client.RestClient;

import java.util.List;
import java.util.UUID;

@Slf4j
public class KmClient {

    private final RestClient restClient;

    public KmClient(RestClient restClient) {
        this.restClient = restClient;
    }

    public void syncUserPermissions(UUID systemUserId, List<String> capabilities, List<String> systemRoles) {
        log.debug("Syncing permissions to KM for user {}", systemUserId);
        SyncPermissionsRequest body = new SyncPermissionsRequest(capabilities, systemRoles);
        restClient.put()
                .uri("/v1/user/{systemUserId}/permissions", systemUserId)
                .body(body)
                .retrieve()
                .toBodilessEntity();
        log.debug("Permissions synced to KM for user {}", systemUserId);
    }

    record SyncPermissionsRequest(List<String> capabilities, List<String> systemRoles) {}
}
