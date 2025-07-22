package com.example.accesscontrolmanager.controller.response;

import com.example.accesscontrolmanager.domain.enums.InheritanceType;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;
import lombok.Data;
import lombok.extern.jackson.Jacksonized;

/**
 * RoleInheritance Response.
 */
@Data
@Builder
@Jacksonized
@JsonIgnoreProperties(ignoreUnknown = true)
public class RoleInheritanceResponse {

    private Long id;
    private RoleResponse childRole;
    @JsonProperty("inheritanceTypeCode")
    private InheritanceType inheritanceType;
    @JsonProperty("roleAssignmentScopeCode")
    private RoleResponse roleResponse;
}
