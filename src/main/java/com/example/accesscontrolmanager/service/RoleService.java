package com.example.accesscontrolmanager.service;

import com.example.accesscontrolmanager.controller.response.AllRolesResponse;
import com.example.accesscontrolmanager.controller.response.RoleResponse;
import com.example.accesscontrolmanager.domain.Role;
import com.example.accesscontrolmanager.domain.enums.RoleTypeCode;

import java.util.List;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Stream;

/**
 * Role Service.
 *
 * <p>
 *     Service layer to perform actions on a Role Entity.
 * </p>
 */
public interface RoleService {

    /**
     * Find a role by ID
     *
     * @param id Long
     * @return RoleResponse
     * @throws jakarta.persistence.EntityNotFoundException when an entity is not found
     */
    RoleResponse find(Long id);

    /**
     * Find roles by name
     *
     * @param name String
     * @return List of RoleResponse
     */
    List<RoleResponse> find(String name);

    /**
     * Find roles by system role id
     *
     * @param id Long
     * @return List of RoleResponse
     */
    List<RoleResponse> findCapabilityRolesBySystemRoleId(Long id);

    /**
     * Find roles by roleTypeCode
     *
     * @param roleTypeCode String
     * @return List of RoleResponse
     */
    List<RoleResponse> findByRoleTypeCode(RoleTypeCode roleTypeCode);

    /**
     * Get all roles
     *
     * @return List of RoleResponse
     */
    List<RoleResponse> findAllActive();

    /**
     * Get the roles for a user.
     *
     * @param systemUserId the user's system ID
     * @return DTO of the roles for the user
     */
    List<RoleResponse> getRoles(UUID systemUserId);

    /**
     * Get all the roles, including inherited roles for a user.
     *
     * @param systemUserId the user's system ID
     * @return DTO of all the roles for the user
     */
    AllRolesResponse getAllRoles(UUID systemUserId);

    Stream<Role> getInheritedRoles(Set<Role> roles);
}
