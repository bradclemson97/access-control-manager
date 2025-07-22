package com.example.accesscontrolmanager.mapper;

import com.example.accesscontrolmanager.controller.request.UserRoleRequest;
import com.example.accesscontrolmanager.domain.User;
import com.example.accesscontrolmanager.domain.UserRole;
import com.example.accesscontrolmanager.model.UserRoleDto;
import com.example.accesscontrolmanager.repository.RoleRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

/**
 * UserRoleMapper.
 */
@Component
@RequiredArgsConstructor
public class UserRoleMapperImpl implements UserRoleMapper {

    private final RoleRepository roleRepository;
    private final RoleFlatMapper roleFlatMapper;
    private final UserMapper userMapper;

    @Override
    public UserRole map(User user, UserRoleRequest request) {
        return UserRole.builder()
                .id(request.getId())
                .user(user)
                .role(roleRepository.findById(request.getRoleId())
                        .orElseThrow(() -> new EntityNotFoundException(
                                "Role with id " + request.getRoleId() + " does not exist")))
                .build();
    }

    @Override
    public UserRoleDto map(UserRole entity) {
        return UserRoleDto.builder()
                .userDto(userMapper.userToDto(entity.getUser()))
                .roleResponse(roleFlatMapper.entityToDto(entity.getRole()))
                .build();
    }
}
