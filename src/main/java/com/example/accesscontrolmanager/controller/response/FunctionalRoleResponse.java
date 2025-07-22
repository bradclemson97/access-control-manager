package com.example.accesscontrolmanager.controller.response;

import com.example.accesscontrolmanager.domain.enums.RoleTypeCode;
import lombok.Builder;
import lombok.Data;
import lombok.extern.jackson.Jacksonized;

/**
 * DTO containing information regarding the User's System Functional Access Roles.
 */
@Data
@Builder
@Jacksonized
public class FunctionalRoleResponse {
    private Long id;
    private String code;
    private RoleTypeCode typeCode;
}
