package com.example.accesscontrolmanager.model;

import com.example.accesscontrolmanager.controller.response.FunctionalRoleResponse;
import com.example.accesscontrolmanager.domain.enums.YesNo;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Builder;
import lombok.Data;
import lombok.extern.jackson.Jacksonized;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

import static com.example.accesscontrolmanager.domain.enums.YesNo.NA;

/**
 * The user data transfer object.
 */
@Data
@Builder
@Jacksonized
public class UserDto {

    private UUID systemUserId;
    @Builder.Default
    private YesNo locked = NA;
    @Builder.Default
    private Set<FunctionalRoleResponse> permissions = new HashSet<>();
}
