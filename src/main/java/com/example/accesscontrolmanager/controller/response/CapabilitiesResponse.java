package com.example.accesscontrolmanager.controller.response;

import lombok.Builder;
import lombok.Data;
import lombok.extern.jackson.Jacksonized;

import java.util.Set;

/**
 * Response containing the set of capability codes held by a user,
 * resolved by traversing the full role inheritance hierarchy.
 */
@Data
@Builder
@Jacksonized
public class CapabilitiesResponse {

    private Set<String> capabilities;
}
