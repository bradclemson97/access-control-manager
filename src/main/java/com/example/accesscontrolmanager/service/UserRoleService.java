package com.example.accesscontrolmanager.service;

import com.example.accesscontrolmanager.controller.request.UserRoleRequest;
import com.example.accesscontrolmanager.model.UserRoleDto;

import java.util.List;
import java.util.UUID;

/**
 * Service to manage the relationship between Users and Roles
 */
public interface UserRoleService {

    /**
     * Save a list of user to role.
     *
     * @param systemUserId UUID
     * @param requests a List of UserRoleRequest
     * @return a List of UserRoleDto
     */
    List<UserRoleDto> save(UUID systemUserId, UUID assignerUserId, List<UserRoleRequest> requests);

    /**
     * Return a user to role with the given ID
     *
     * @param id Long
     * @return UserRoleDto
     */
    UserRoleDto get(Long id);

    /**
     * Return a list of user to role for a given user.
     *
     * @param systemUserId UUID
     * @return a List of UserRoleDto
     */
    List<UserRoleDto> getByUser(UUID systemUserId);

    /**
     * Return a single UserRoleDto
     *
     * @param id Long
     * @param systemUserId UUID
     * @return UserRoleDto
     */
    UserRoleDto getByIdAndUser(Long id, UUID systemUserId);
}
