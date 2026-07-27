package com.example.accesscontrolmanager.controller;

import com.example.accesscontrolmanager.controller.request.CreateUserRequest;
import com.example.accesscontrolmanager.controller.response.CapabilitiesResponse;
import com.example.accesscontrolmanager.controller.response.CreateUserResponse;
import com.example.accesscontrolmanager.exception.response.ApiError;
import com.example.accesscontrolmanager.model.UserDto;
import com.example.security.annotation.RequiresCapability;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

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
@RequestMapping(API_VERSION + API_USER)
public interface UserController {

    @Operation(summary = "Create new user", description = "Create a new user")
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @ApiResponse(responseCode = "201", description = "User creation successful")
    @RequiresCapability("Create users")
    CreateUserResponse createUser(
            @RequestBody @Valid CreateUserRequest userRequest);

    @Operation(summary = "Get user", description = "Get user information for the provided systemUserId")
    @GetMapping("{systemUserId}")
    @RequiresCapability("Search and View users")
    UserDto getUser(
            @Parameter(example = "5dad5a08-73a2-5eds-2sdh-99fab505402a",
            description = "The user's unique reference")
            @PathVariable UUID systemUserId);

    @Operation(summary = "Get current user",
            description = "Get current user information via session token claims")
    @GetMapping(API_CURRENT)
    UserDto getCurrentUser(
            @NotNull(message = "Session token is required")
            @AuthenticationPrincipal(expression = "systemUserId")
            UUID systemUserId);

    @RequiresCapability("Manage users account")
    @Operation(summary = "Lock user account", description = "Sets the user's locked status to YES")
    @PutMapping("{systemUserId}" + API_LOCK)
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @ApiResponse(responseCode = "204", description = "User locked successfully")
    void lockUser(
            @Parameter(example = "5dad5a08-73a2-5eds-2sdh-99fab505402a",
                    description = "The user's unique reference")
            @PathVariable UUID systemUserId);

    @RequiresCapability("Manage users account")
    @Operation(summary = "Unlock user account", description = "Clears the user's lock flag and failed attempt counter")
    @DeleteMapping("{systemUserId}" + API_LOCK)
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @ApiResponse(responseCode = "204", description = "User unlocked successfully")
    void unlockUser(
            @Parameter(example = "5dad5a08-73a2-5eds-2sdh-99fab505402a",
                    description = "The user's unique reference")
            @PathVariable UUID systemUserId);

    @Operation(summary = "Get user capabilities",
            description = "Returns the flat set of capability codes held by the user, "
                    + "resolved by traversing the full role inheritance hierarchy. "
                    + "This endpoint is called by the security-library to populate the "
                    + "Spring SecurityContext on each request.")
    @GetMapping("{systemUserId}/capabilities")
    @ApiResponse(responseCode = "200", description = "Capabilities retrieved successfully")
    CapabilitiesResponse getCapabilities(
            @Parameter(example = "5dad5a08-73a2-5eds-2sdh-99fab505402a",
                    description = "The user's unique reference")
            @PathVariable UUID systemUserId);

}
