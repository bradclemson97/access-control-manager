package com.example.accesscontrolmanager.controller.response;

import com.example.accesscontrolmanager.domain.enums.RoleTypeCode;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;
import lombok.Data;
import lombok.extern.jackson.Jacksonized;

import java.util.List;

/**
 * Role Response.
 */
@Data
@Builder
@Jacksonized
@JsonIgnoreProperties(ignoreUnknown = true)
public class RoleResponse {

    private Long id;
    private String roleName;
    private RoleTypeCode roleTypeCode;
    private String description;
    private List<RolePrivilegeResponse> rolePrivileges;
    @JsonProperty("roleInheritance")
    private List<RoleInheritanceResponse> roleInheritances;
}
