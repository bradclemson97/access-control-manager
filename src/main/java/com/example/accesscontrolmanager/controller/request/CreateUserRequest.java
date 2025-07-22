package com.example.accesscontrolmanager.controller.request;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Data;
import lombok.extern.jackson.Jacksonized;

import java.util.UUID;

@Data
@Builder
@Jacksonized
public class CreateUserRequest {

    @Schema(description = "The user ID of the user", requiredMode = Schema.RequiredMode.REQUIRED)
    @Builder.Default
    private UUID systemUserId = UUID.randomUUID();

}
