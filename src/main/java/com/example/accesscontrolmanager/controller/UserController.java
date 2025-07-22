package com.example.accesscontrolmanager.controller;

import com.example.accesscontrolmanager.controller.request.CreateUserRequest;
import com.example.accesscontrolmanager.controller.response.CreateUserResponse;
import com.example.accesscontrolmanager.exception.response.ApiError;
import com.example.accesscontrolmanager.model.UserDto;
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
    CreateUserResponse createUser(
            @RequestBody @Valid CreateUserRequest userRequest);

    @Operation(summary = "Get user", description = "Get user information for the provided systemUserId")
    @GetMapping("{systemUserId}")
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


}
