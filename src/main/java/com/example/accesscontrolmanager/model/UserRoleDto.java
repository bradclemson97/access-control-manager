package com.example.accesscontrolmanager.model;

import com.example.accesscontrolmanager.controller.response.RoleResponse;
import lombok.Builder;
import lombok.Data;
import lombok.extern.jackson.Jacksonized;

/**
 * DTO for UserRole
 */
@Data
@Builder
@Jacksonized
public class UserRoleDto {

    private UserDto userDto;
    private RoleResponse roleResponse;
}
