package com.example.accesscontrolmanager.controller.response;

import lombok.Builder;
import lombok.Data;
import lombok.extern.jackson.Jacksonized;

/**
 * Role
 */
@Data
@Builder
@Jacksonized
public class RolePrivilegeResponse {

    private Long id;
    private String description;
    private RoleResponse roleResponse;
}
