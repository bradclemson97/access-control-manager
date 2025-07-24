package com.example.accesscontrolmanager.controller.response;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Builder;
import lombok.Data;
import lombok.extern.jackson.Jacksonized;

import java.util.Set;

/**
 * All Roles Response
 */
@Data
@Builder
@Jacksonized
@JsonIgnoreProperties(ignoreUnknown = true)
public class AllRolesResponse {

    private Set<FunctionalRoleResponse> roles;
    private Set<RoleAssignmentResponse> roleAssignments;
}
