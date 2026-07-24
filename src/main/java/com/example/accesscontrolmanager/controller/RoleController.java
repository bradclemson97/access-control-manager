package com.example.accesscontrolmanager.controller;

import com.example.accesscontrolmanager.controller.response.AllRolesResponse;
import com.example.accesscontrolmanager.controller.response.RoleResponse;
import com.example.accesscontrolmanager.domain.enums.RoleTypeCode;
import com.example.accesscontrolmanager.exception.response.ApiError;
import com.example.security.annotation.RequiresCapability;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;
import java.util.UUID;

import static com.example.accesscontrolmanager.config.SystemConstant.API_ROLE;
import static com.example.accesscontrolmanager.config.SystemConstant.API_VERSION;
import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;

/**
 * APIs for querying Roles.
 */
@Tag(name = "Role Controller", description = "Controller for querying Roles and Role hierarchies")
@ApiResponse(responseCode = "400", description = "Invalid request", content = @Content(
        mediaType = APPLICATION_JSON_VALUE, schema = @Schema(implementation = ApiError.class)))
@ApiResponse(responseCode = "401", description = "Unauthorised", content = @Content(
        mediaType = APPLICATION_JSON_VALUE, schema = @Schema(implementation = ApiError.class)))
@ApiResponse(responseCode = "404", description = "Entity not found", content = @Content(
        mediaType = APPLICATION_JSON_VALUE, schema = @Schema(implementation = ApiError.class)))
@ApiResponse(responseCode = "500", description = "Internal Server Error", content = @Content(
        mediaType = APPLICATION_JSON_VALUE, schema = @Schema(implementation = ApiError.class)))
@Validated
@RequestMapping(API_VERSION + API_ROLE)
public interface RoleController {

    @Operation(summary = "Get roles",
            description = "Get all roles. Optionally filter by name (partial match) or typeCode. "
                    + "If both are provided, name takes precedence.")
    @GetMapping
    @RequiresCapability("Search and View users")
    List<RoleResponse> findAll(
            @Parameter(description = "Partial role name to search for")
            @RequestParam(required = false) String name,
            @Parameter(description = "Filter by role type code")
            @RequestParam(required = false) RoleTypeCode typeCode);

    @Operation(summary = "Get role by ID", description = "Get a single role by its ID")
    @GetMapping("{id}")
    @ApiResponse(responseCode = "200", description = "Role found")
    @RequiresCapability("Search and View users")
    RoleResponse findById(
            @Parameter(description = "The role's ID", example = "1")
            @PathVariable Long id);

    @Operation(summary = "Get capability roles",
            description = "Get the capability roles that a system role inherits")
    @GetMapping("{id}/capabilities")
    @RequiresCapability("Search and View users")
    List<RoleResponse> findCapabilities(
            @Parameter(description = "The system role's ID", example = "1")
            @PathVariable Long id);

    @Operation(summary = "Get user's direct roles",
            description = "Get the roles directly assigned to a user")
    @GetMapping("user/{systemUserId}")
    @RequiresCapability("Search and View users")
    List<RoleResponse> getUserRoles(
            @Parameter(description = "The user's system ID",
                    example = "5dad5a08-73a2-5eds-2sdh-99fab505402a")
            @PathVariable UUID systemUserId);

    @Operation(summary = "Get all user roles including inherited",
            description = "Get all roles and role assignments for a user, including roles "
                    + "inherited via the role hierarchy")
    @GetMapping("user/{systemUserId}/all")
    @RequiresCapability("Search and View users")
    AllRolesResponse getAllUserRoles(
            @Parameter(description = "The user's system ID",
                    example = "5dad5a08-73a2-5eds-2sdh-99fab505402a")
            @PathVariable UUID systemUserId);
}
