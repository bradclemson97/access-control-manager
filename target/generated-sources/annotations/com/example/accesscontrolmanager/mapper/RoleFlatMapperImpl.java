package com.example.accesscontrolmanager.mapper;

import com.example.accesscontrolmanager.controller.response.RoleResponse;
import com.example.accesscontrolmanager.domain.Role;
import javax.annotation.processing.Generated;
import org.springframework.stereotype.Component;

@Generated(
    value = "org.mapstruct.ap.MappingProcessor",
    comments = "version: 1.5.5.Final, compiler: javac, environment: Java 17.0.20.1 (Homebrew)"
)
@Component
public class RoleFlatMapperImpl implements RoleFlatMapper {

    @Override
    public RoleResponse entityToDto(Role entity) {
        if ( entity == null ) {
            return null;
        }

        RoleResponse.RoleResponseBuilder roleResponse = RoleResponse.builder();

        roleResponse.id( entity.getId() );
        roleResponse.roleName( entity.getRoleName() );
        roleResponse.roleTypeCode( entity.getRoleTypeCode() );
        roleResponse.description( entity.getDescription() );

        return roleResponse.build();
    }
}
