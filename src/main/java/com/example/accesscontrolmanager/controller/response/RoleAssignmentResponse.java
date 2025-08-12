package com.example.accesscontrolmanager.controller.response;

import lombok.Builder;
import lombok.Data;
import lombok.extern.jackson.Jacksonized;

/**
 * DTO containing information regarding the User's system functional access roles.
 */
@Data
@Builder
@Jacksonized
public class RoleAssignmentResponse {
    private String code;
}
