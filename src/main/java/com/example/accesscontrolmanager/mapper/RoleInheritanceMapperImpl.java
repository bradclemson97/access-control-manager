package com.example.accesscontrolmanager.mapper;

import com.example.accesscontrolmanager.controller.response.RoleInheritanceResponse;
import com.example.accesscontrolmanager.domain.RoleInheritance;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.Collections;
import java.util.List;
import java.util.Set;

/**
 * Mapper-level for RoleInheritance.
 */
@Component
@RequiredArgsConstructor
public class RoleInheritanceMapperImpl implements RoleInheritanceMapper {

    private final RoleFlatMapper roleFlatMapper;

    @Override
    public RoleInheritanceResponse map(RoleInheritance entity) {
        final RoleInheritanceResponse response = RoleInheritanceResponse.builder().build();
        response.setId(entity.getId());
        response.setInheritanceType(entity.getInheritanceType());
        response.setRoleResponse(roleFlatMapper.entityToDto(entity.getParentRole()));
        response.setChildRole(roleFlatMapper.entityToDto(entity.getChildRole()));
        return response;
    }

    @Override
    public List<RoleInheritanceResponse> map(Set<RoleInheritance> entities) {
        if (entities != null) {
            return entities.stream().map(
                    this::map
            ).filter(e -> e.getId() != null).toList();
        }
        return Collections.emptyList();
    }
}
