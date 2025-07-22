package com.example.accesscontrolmanager.mapper;

import com.example.accesscontrolmanager.controller.request.UserRoleRequest;
import com.example.accesscontrolmanager.domain.User;
import com.example.accesscontrolmanager.domain.UserRole;
import com.example.accesscontrolmanager.model.UserRoleDto;

/**
 * Map for UserRoleMapper entities to UserRoleRequest and UserRoleDto and the other way
 * round.
 */
public interface UserRoleMapper {

    /**
     * map a request to an entity
     *
     * @param user User
     * @param request UserRoleRequest
     * @return UserRole
     */
    UserRole map(User user, UserRoleRequest request);

    /**
     * map an entity to a response
     *
     * @param entity UserRole
     * @return UserRoleDto
     */
    UserRoleDto map(UserRole entity);

}
