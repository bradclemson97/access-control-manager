package com.example.accesscontrolmanager.controller;

import com.example.accesscontrolmanager.controller.request.UserRoleListRequest;
import com.example.accesscontrolmanager.exception.response.ApiError;
import com.example.accesscontrolmanager.model.UserRoleDto;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

import static com.example.accesscontrolmanager.config.SystemConstant.*;
import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;

/**
 * APIs for retrieving and managing Users.
 */
@Tag(name = "User Controller", description = "Controller for managing Users")
@ApiResponse(responseCode = "400", description = "Invalid request payload", content = @Content(
        mediaType = APPLICATION_JSON_VALUE, schema = @Schema(implementation = ApiError.class)))
@ApiResponse(responseCode = "401", description = "Unauthorised", content = @Content(
        mediaType = APPLICATION_JSON_VALUE, schema = @Schema(implementation = ApiError.class)))
@ApiResponse(responseCode = "404", description = "Entity not found", content = @Content(
        mediaType = APPLICATION_JSON_VALUE, schema = @Schema(implementation = ApiError.class)))
@ApiResponse(responseCode = "415", description = "Unsupported Media Type", content = @Content(
        mediaType = APPLICATION_JSON_VALUE, schema = @Schema(implementation = ApiError.class)))
@ApiResponse(responseCode = "500", description = "Internal Server Error", content = @Content(
        mediaType = APPLICATION_JSON_VALUE, schema = @Schema(implementation = ApiError.class)))
@Validated
@RequestMapping(API_VERSION + API_USER_ROLE)
public interface UserRoleController {

    @Operation(summary = "save user roles",
    description = "Save user roles with the provided userRoleListRequest and returns a List of userRoleDto.")
    @PostMapping
    List<UserRoleDto> save(
            @Parameter(example = "5dad5a08-73a2-5eds-2sdh-99fab505402a", required = true,
                    description = "The user's unique reference")
            @PathVariable UUID systemUserId,
            // TO DO: create session claims library class and use that to retrieve systemUserId
            // @AuthenticationPrincipal SessionClaims claims, for now we will pass in as a parameter
            @Parameter(example = "5dad5a08-73a2-5eds-2sdh-99fab505402a",
                    description = "The user's unique reference")
            @PathVariable UUID assignerUserId,
            @RequestBody @Valid UserRoleListRequest request);

    @Operation(summary = "Get a user role",
    description = "Get a user role with the provided user id and user to role id and returns "
    + "a UserRoleDto.")
    @GetMapping("{id}")
    UserRoleDto get(
            @Parameter(example = "5dad5a08-73a2-5eds-2sdh-99fab505402a", required = true,
                    description = "The user's unique reference")
            @PathVariable UUID systemUserId,
            @PathVariable Long id);

    @Operation(summary = "Get a list of user roles",
            description = "Get a list of user role with the provided user id and user to role id and returns "
            + "a List of UserRoleDto.")
    @GetMapping
    List<UserRoleDto> getByUser(
            @Parameter(example = "5dad5a08-73a2-5eds-2sdh-99fab505402a", required = true,
                    description = "The user's unique reference")
            @PathVariable UUID systemUserId);
}
