package com.example.accesscontrolmanager.mapper;

import com.example.accesscontrolmanager.controller.response.RoleResponse;
import com.example.accesscontrolmanager.domain.Role;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.NullValuePropertyMappingStrategy;

/**
 * A mapper for Role without nested classes.
 */
@Mapper(componentModel = "spring",
    nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
public interface RoleFlatMapper {

    /**
     * Map a Role entity to a RoleResponse without nested classes.
     *
     * @param entity role
     * @return RoleResponse
     */
    @Mapping(target = "rolePrivileges", ignore = true)
    @Mapping(target = "roleInheritances", ignore = true)
    RoleResponse entityToDto(Role entity);
}
