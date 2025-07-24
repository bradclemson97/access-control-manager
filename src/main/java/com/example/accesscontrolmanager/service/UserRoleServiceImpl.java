package com.example.accesscontrolmanager.service;

import com.example.accesscontrolmanager.controller.request.UserRoleRequest;
import com.example.accesscontrolmanager.domain.Role;
import com.example.accesscontrolmanager.domain.RoleInheritance;
import com.example.accesscontrolmanager.domain.User;
import com.example.accesscontrolmanager.domain.UserRole;
import com.example.accesscontrolmanager.exception.RoleAssignmentNotAllowedException;
import com.example.accesscontrolmanager.mapper.UserRoleMapper;
import com.example.accesscontrolmanager.model.UserRoleDto;
import com.example.accesscontrolmanager.repository.UserRoleRepository;
import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

/**
 * Service to manage the relationship between Users and Roles.
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class UserRoleServiceImpl implements UserRoleService {
    private final UserRoleRepository userRoleRepository;
    private final UserRoleMapper userRoleMapper;
    private final UserService userService;

    @Override
    @Transactional
    public List<UserRoleDto> save(UUID systemUserId, UUID assignerUserId, List<UserRoleRequest> requests) {
        //Get the assigner
        User assigner = userService.getUser(assignerUserId);
        //... and all the roles that are allowed to assign
        Set<Role> assignableRoles = getAssignableRoles(assigner);

        // get the user to assign roles to.
        User assigneeUser = userService.getUser(systemUserId);
        // ... and all their currently active roles
        Set<UserRole> userRoles = assigneeUser.getUserRoles();

        Set<UserRole> requestedUserRoles = requests.stream()
                .map(request -> userRoleMapper.map(assigneeUser, request))
                .collect(Collectors.toSet());

        // get all the roles that we are trying to remove from the assignee
        // i.e. roles that are in the existing set of roles but not in the
        // requestedUserRoles we want to assign
        Set<UserRole> toRemove =
                userRoles.stream()
                        .filter(existingRole -> doesNotMatch(existingRole, requestedUserRoles))
                        .collect(Collectors.toSet());

        // check that they can all be removed before we remove any
        if (anyRolesNotAssignable(assignableRoles, toRemove)) {
            throw new RoleAssignmentNotAllowedException("Existing role cannot be removed.");
        }

        // Get the set of new UserRoles, i.e. those we are trying to add
        // and that the user does not already have.
        Set<UserRole> newUserRoles = requestedUserRoles.stream()
                .filter(it -> doesNotMatch(it, userRoles))
                .collect(Collectors.toSet());

        // Make sure that all the new roles we need to
        // give to the user are allowed to be assigned.
        if (anyRolesNotAssignable(assignableRoles, newUserRoles)) {
            throw new RoleAssignmentNotAllowedException("New role cannot be assigned.");
        }

        userRoles.removeAll(toRemove);
        userRoles.addAll(newUserRoles);

        return userRoles.stream()
                .map(userRoleMapper::map)
                .toList();
    }

    @Override
    @Transactional
    public UserRoleDto get(Long id) {
        UserRole userRole = userRoleRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("UserRole not found"));
        return userRoleMapper.map(userRole);
    }

    @Override
    @Transactional
    public List<UserRoleDto> getByUser(UUID systemUserId) {
        User user = userService.getUser(systemUserId);
        Set<UserRole> activeUserRoles = user.getUserRoles();
        return activeUserRoles.stream()
                .map(userRoleMapper::map)
                .toList();
    }

    @Override
    @Transactional
    public UserRoleDto getByIdAndUser(Long id, UUID systemUserId) {
        User user = userService.getUser(systemUserId);
        Set<UserRole> userRoles = user.getUserRoles();
        UserRole userRole = userRoles.stream()
                .filter(role -> role.getId().equals(id))
                .findFirst()
                .orElseThrow(EntityNotFoundException::new);
        return userRoleMapper.map(userRole);
    }

    /**
     * Return true if the given UserRole is not in the provided set of
     * userRoles (being that they have the sme role ID and userId).
     *
     * @param userRole role to check
     * @param userRoles set of user roles
     * @return true if userRole is not in the existingUserRoles
     */
    private boolean doesNotMatch(UserRole userRole, Set<UserRole> userRoles) {
        return userRoles.stream().noneMatch(it ->
                Objects.equals(it.getUser().getId(), userRole.getUser().getId())
        && Objects.equals(it.getRole().getId(), userRole.getRole().getId()));
    }

    /**
     * Return the set of roles that this given user is allowed to assign.
     *
     * @param assigner the user
     * @return Set of roles the user can assign
     */
    private Set<Role> getAssignableRoles(User assigner) {
        return assigner.getRoles()
                .stream()
                .map(Role::getAssignments)
                .flatMap(Collection::stream)
                .map(RoleInheritance::getChildRole)
                .collect(Collectors.toSet());
    }

    private boolean anyRolesNotAssignable(Set<Role> assignableRoles, Set<UserRole> requests) {
        // check if the given user "assigner" is allowed to assign
        // all the roles they are trying to
        Set<Long> assignableRoleIds = assignableRoles
                .stream()
                .map(Role::getId)
                .collect(Collectors.toSet());

        Set<Long> roleIdsToAssign = requests.stream()
                .map(UserRole::getRole)
                .map(Role::getId)
                .collect(Collectors.toSet());

        return !assignableRoleIds.containsAll(roleIdsToAssign);
    }
}
