package com.example.accesscontrolmanager.mapper;

import com.example.accesscontrolmanager.controller.response.FunctionalRoleResponse;
import com.example.accesscontrolmanager.controller.response.RoleAssignmentResponse;
import com.example.accesscontrolmanager.controller.response.RoleResponse;
import com.example.accesscontrolmanager.domain.Role;
import com.example.accesscontrolmanager.domain.RoleInheritance;
import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;
import javax.annotation.processing.Generated;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Generated(
    value = "org.mapstruct.ap.MappingProcessor",
    comments = "version: 1.5.5.Final, compiler: javac, environment: Java 17.0.16 (Oracle Corporation)"
)
@Component
public class RoleMapperImpl implements RoleMapper {

    @Autowired
    private RolePrivilegeMapper rolePrivilegeMapper;
    @Autowired
    private RoleInheritanceMapper roleInheritanceMapper;

    @Override
    public List<RoleResponse> rolesToDtos(Set<Role> roles) {
        if ( roles == null ) {
            return new ArrayList<RoleResponse>();
        }

        List<RoleResponse> list = new ArrayList<RoleResponse>( roles.size() );
        for ( Role role : roles ) {
            list.add( roleToDto( role ) );
        }

        return list;
    }

    @Override
    public RoleResponse roleToDto(Role role) {
        if ( role == null ) {
            return null;
        }

        RoleResponse.RoleResponseBuilder roleResponse = RoleResponse.builder();

        roleResponse.roleInheritances( roleInheritanceMapper.map( role.getRoleInheritances() ) );
        roleResponse.id( role.getId() );
        roleResponse.roleName( role.getRoleName() );
        roleResponse.roleTypeCode( role.getRoleTypeCode() );
        roleResponse.description( role.getDescription() );
        roleResponse.rolePrivileges( rolePrivilegeMapper.map( role.getRolePrivileges() ) );

        return roleResponse.build();
    }

    @Override
    public Set<FunctionalRoleResponse> rolesToFunctionalDtos(Set<Role> roles) {
        if ( roles == null ) {
            return new LinkedHashSet<FunctionalRoleResponse>();
        }

        Set<FunctionalRoleResponse> set = new LinkedHashSet<FunctionalRoleResponse>( Math.max( (int) ( roles.size() / .75f ) + 1, 16 ) );
        for ( Role role : roles ) {
            set.add( roleToFunctionalDto( role ) );
        }

        return set;
    }

    @Override
    public FunctionalRoleResponse roleToFunctionalDto(Role role) {
        if ( role == null ) {
            return null;
        }

        FunctionalRoleResponse.FunctionalRoleResponseBuilder functionalRoleResponse = FunctionalRoleResponse.builder();

        functionalRoleResponse.code( role.getRoleName() );
        functionalRoleResponse.typeCode( role.getRoleTypeCode() );
        functionalRoleResponse.id( role.getId() );

        return functionalRoleResponse.build();
    }

    @Override
    public Set<RoleAssignmentResponse> inheritancesToDtos(Set<RoleInheritance> inheritances) {
        if ( inheritances == null ) {
            return new LinkedHashSet<RoleAssignmentResponse>();
        }

        Set<RoleAssignmentResponse> set = new LinkedHashSet<RoleAssignmentResponse>( Math.max( (int) ( inheritances.size() / .75f ) + 1, 16 ) );
        for ( RoleInheritance roleInheritance : inheritances ) {
            set.add( inheritanceToDto( roleInheritance ) );
        }

        return set;
    }

    @Override
    public RoleAssignmentResponse inheritanceToDto(RoleInheritance inheritance) {
        if ( inheritance == null ) {
            return null;
        }

        RoleAssignmentResponse.RoleAssignmentResponseBuilder roleAssignmentResponse = RoleAssignmentResponse.builder();

        roleAssignmentResponse.code( inheritanceChildRoleRoleName( inheritance ) );

        return roleAssignmentResponse.build();
    }

    private String inheritanceChildRoleRoleName(RoleInheritance roleInheritance) {
        if ( roleInheritance == null ) {
            return null;
        }
        Role childRole = roleInheritance.getChildRole();
        if ( childRole == null ) {
            return null;
        }
        String roleName = childRole.getRoleName();
        if ( roleName == null ) {
            return null;
        }
        return roleName;
    }
}
