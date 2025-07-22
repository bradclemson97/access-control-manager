package com.example.accesscontrolmanager.mapper;

import com.example.accesscontrolmanager.controller.response.RolePrivilegeResponse;
import com.example.accesscontrolmanager.domain.RolePrivilege;

import java.util.List;
import java.util.Set;

/**
 * Map RolePrivilege entities to responses and other way round
 */
public interface RolePrivilegeMapper {

    /**
     * Map a RolePrivilege entity to a RolePrivilegeResponse
     *
     * @param entity RolePrivilege
     * @return RolePrivilegeResponse
     */
    RolePrivilegeResponse map(RolePrivilege entity);

    /**
     * Map a set of RolePrivilege entities to a list of RolePrivilegeResponse.
     *
     * @param entities Map of RolePrivilege
     * @return List of RolePrivilegeResponse
     */
    List<RolePrivilegeResponse> map(Set<RolePrivilege> entities);
}
