package com.example.accesscontrolmanager.controller.request;

import jakarta.validation.constraints.NotNull;
import lombok.Builder;
import lombok.Data;
import lombok.extern.jackson.Jacksonized;

import java.util.UUID;

@Data
@Builder
@Jacksonized
public class UserRoleRequest {

    private Long id;
    @NotNull(message = "The field roleId is required")
    private Long roleId;
    private UUID systemUserId;
}
