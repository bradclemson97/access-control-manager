package com.example.accesscontrolmanager.mapper;

import com.example.accesscontrolmanager.controller.response.FunctionalRoleResponse;
import com.example.accesscontrolmanager.controller.response.RoleAssignmentResponse;
import com.example.accesscontrolmanager.controller.response.RoleResponse;
import com.example.accesscontrolmanager.domain.Role;
import com.example.accesscontrolmanager.domain.RoleInheritance;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.NullValueMappingStrategy;
import org.mapstruct.NullValuePropertyMappingStrategy;

import java.util.List;
import java.util.Set;

/**
 * Map Role entities to response and the other way round.
 */
@Mapper(componentModel = "spring",
    nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE,
    nullValueIterableMappingStrategy = NullValueMappingStrategy.RETURN_DEFAULT,
    uses = {RolePrivilegeMapper.class, RoleInheritanceMapper.class})
public interface RoleMapper {

    /**
     * Map a set of Role entities to a list of RoleDTOs.
     *
     * @param roles Map of Role
     * @return List of RoleResponse
     */
    List<RoleResponse> rolesToDtos(Set<Role> roles);

    /**
     * Map a Role entity to a RoleResponse.
     *
     * @param role Role
     * @return RoleResponse
     */
    @Mapping(source = "roleInheritances", target = "roleInheritances")
    RoleResponse roleToDto(Role role);

    Set<FunctionalRoleResponse> rolesToFunctionalDtos(Set<Role> roles);

    @Mapping(source = "roleName", target = "code")
    @Mapping(source = "roleTypeCode", target = "typeCode")
    FunctionalRoleResponse roleToFunctionalDto(Role role);

    Set<RoleAssignmentResponse> inheritancesToDtos(Set<RoleInheritance> inheritances);

    @Mapping(source = "childRole.roleName", target = "code")
    RoleAssignmentResponse inheritanceToDto(RoleInheritance inheritance);
}
