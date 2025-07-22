package com.example.accesscontrolmanager.mapper;

import com.example.accesscontrolmanager.controller.response.RoleInheritanceResponse;
import com.example.accesscontrolmanager.domain.RoleInheritance;

import java.util.List;
import java.util.Set;

public interface RoleInheritanceMapper {

    /**
     * Map a RoleInheritance entity to a RoleInheritanceResponse.
     *
     * @param entity RoleInheritance
     * @return RoleInheritanceResponse
     */
    RoleInheritanceResponse map(RoleInheritance entity);

    /**
     * Map a set of RoleInheritance entities to a list of RoleInheritanceDTOs.
     *
     * @param entities Map of RoleInheritance
     * @return List of RoleInheritanceResponse
     */
    List<RoleInheritanceResponse> map(Set<RoleInheritance> entities);
}
