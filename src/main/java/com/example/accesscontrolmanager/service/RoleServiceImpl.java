package com.example.accesscontrolmanager.service;

import com.example.accesscontrolmanager.controller.response.AllRolesResponse;
import com.example.accesscontrolmanager.controller.response.FunctionalRoleResponse;
import com.example.accesscontrolmanager.controller.response.RoleAssignmentResponse;
import com.example.accesscontrolmanager.controller.response.RoleResponse;
import com.example.accesscontrolmanager.domain.Role;
import com.example.accesscontrolmanager.domain.RoleInheritance;
import com.example.accesscontrolmanager.domain.User;
import com.example.accesscontrolmanager.domain.enums.RoleTypeCode;
import com.example.accesscontrolmanager.mapper.RoleMapper;
import com.example.accesscontrolmanager.repository.RoleInheritanceRepository;
import com.example.accesscontrolmanager.repository.RoleRepository;
import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * RoleService.
 *
 * <p>
 *     Service layer to perform actions on a Role entity.
 * </p>
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class RoleServiceImpl implements RoleService {

    private final RoleRepository roleRepository;
    private final RoleMapper roleMapper;
    private final RoleInheritanceRepository roleInheritanceRepository;
    private final UserService userService;

    @Override
    @Transactional
    public RoleResponse find(Long id) {
        final Role entity = roleRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Role with ID " + id + " does not exist"));
        return roleMapper.roleToDto(entity);
    }

    @Override
    @Transactional
    public List<RoleResponse> find(String name) {
        return roleMapper.rolesToDtos(roleRepository.findByRoleNameContains(name));
    }

    @Override
    @Transactional
    public List<RoleResponse> findCapabilityRolesBySystemRoleId(Long id) {
        Set<RoleInheritance> roleInheritances = roleInheritanceRepository.findByParentRoleId(id);
        Set<Role> roleEntities = roleInheritances.stream()
                .map(RoleInheritance::getChildRole)
                .collect(Collectors.toSet());
        return roleMapper.rolesToDtos(roleEntities);
    }

    @Override
    @Transactional
    public List<RoleResponse> findByRoleTypeCode(RoleTypeCode roleTypeCode) {
        Set<Role> roles = roleRepository.findByRoleTypeCode(roleTypeCode);
        return roleMapper.rolesToDtos(roles);
    }

    @Override
    @Transactional
    public List<RoleResponse> findAllActive() {
        Set<Role> entities = new HashSet<>(roleRepository.findAll());
        return roleMapper.rolesToDtos(entities);
    }

    @Override
    @Transactional
    public List<RoleResponse> getRoles(UUID systemUserId) {
        User user = userService.getUser(systemUserId);
        Set<Role> roles = user.getRoles();
        return roleMapper.rolesToDtos(roles);
    }

    @Override
    @Transactional
    public AllRolesResponse getAllRoles(UUID systemUserId) {
        User user = userService.getUser(systemUserId);
        Set<Role> inheritedRoles = getInheritedRoles(user.getRoles())
                .collect(Collectors.toSet());
        Set<FunctionalRoleResponse> inherited = roleMapper.rolesToFunctionalDtos(inheritedRoles);

        Set<RoleInheritance> assignmentRoles = user.getRoles().stream()
                .map(Role::getAssignments)
                .flatMap(Collection::stream)
                .collect(Collectors.toSet());
        Set<RoleAssignmentResponse> assignments = roleMapper.inheritancesToDtos(assignmentRoles);

        return AllRolesResponse.builder()
                .roles(inherited)
                .roleAssignments(assignments)
                .build();
    }

    @Override
    public Stream<Role> getInheritedRoles(Set<Role> roles) {
        Set<Role> children = roles.stream()
                .map(Role::getInheritedRoles)
                .flatMap(Collection::stream)
                .collect(Collectors.toSet());

        Stream<Role> inheritedRoles = children.isEmpty() ? Stream.empty() : getInheritedRoles(children);

        return Stream.concat(roles.stream(), inheritedRoles);
    }


}
