package com.example.accesscontrolmanager.mapper;

import com.example.accesscontrolmanager.controller.response.RolePrivilegeResponse;
import com.example.accesscontrolmanager.domain.RolePrivilege;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

import java.util.List;
import java.util.Set;

@Component
@RequiredArgsConstructor
public class RolePrivilegeMapperImpl implements RolePrivilegeMapper {

    private final RoleFlatMapper roleFlatMapper;

    @Override
    public RolePrivilegeResponse map(RolePrivilege entity) {
        final RolePrivilegeResponse response = RolePrivilegeResponse.builder().build();
        response.setId(entity.getId());
        response.setDescription(entity.getDescription());
        response.setRoleResponse(roleFlatMapper.entityToDto(entity.getRoleForPrivilege()));
        return response;
    }

    @Override
    public List<RolePrivilegeResponse> map(Set<RolePrivilege> entities) {
        if (CollectionUtils.isEmpty(entities)) {
            return List.of();
        }
        return entities.stream()
                .map(this::map)
                .toList();
    }
}
