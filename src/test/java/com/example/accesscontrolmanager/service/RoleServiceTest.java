package com.example.accesscontrolmanager.service;

import com.example.accesscontrolmanager.controller.response.AllRolesResponse;
import com.example.accesscontrolmanager.controller.response.FunctionalRoleResponse;
import com.example.accesscontrolmanager.controller.response.RoleAssignmentResponse;
import com.example.accesscontrolmanager.controller.response.RoleResponse;
import com.example.accesscontrolmanager.domain.Role;
import com.example.accesscontrolmanager.domain.RoleInheritance;
import com.example.accesscontrolmanager.domain.User;
import com.example.accesscontrolmanager.domain.UserRole;
import com.example.accesscontrolmanager.domain.enums.RoleTypeCode;
import com.example.accesscontrolmanager.mapper.RoleMapper;
import com.example.accesscontrolmanager.repository.RoleInheritanceRepository;
import com.example.accesscontrolmanager.repository.RoleRepository;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.*;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.BDDMockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("RoleServiceImpl Unit Tests")
class RoleServiceTest {

    @Mock
    private RoleRepository roleRepository;

    @Mock
    private RoleMapper roleMapper;

    @Mock
    private RoleInheritanceRepository roleInheritanceRepository;

    @Mock
    private UserService userService;

    @InjectMocks
    private RoleServiceImpl roleService;

    private Role role;
    private RoleResponse roleResponse;

    @BeforeEach
    void setUp() {
        role = Role.builder()
                .id(1L)
                .roleName("ADMIN")
                .build();

        roleResponse = RoleResponse.builder()
                .id(1L)
                .roleName("ADMIN")
                .build();
    }

    @Test
    @DisplayName("find(id) - returns RoleResponse when found")
    void findById_success() {
        given(roleRepository.findById(1L)).willReturn(Optional.of(role));
        given(roleMapper.roleToDto(role)).willReturn(roleResponse);

        RoleResponse result = roleService.find(1L);

        assertThat(result).isEqualTo(roleResponse);
    }

    @Test
    @DisplayName("find(id) - throws when not found")
    void findById_notFound() {
        given(roleRepository.findById(1L)).willReturn(Optional.empty());

        assertThatThrownBy(() -> roleService.find(1L))
                .isInstanceOf(EntityNotFoundException.class)
                .hasMessageContaining("Role with ID 1 does not exist");
    }

    @Test
    @DisplayName("find(name) - returns matching roles")
    void findByName() {
        Set<Role> roles = Set.of(role);
        List<RoleResponse> responses = List.of(roleResponse);

        given(roleRepository.findByRoleNameContains("ADMIN")).willReturn(roles);
        given(roleMapper.rolesToDtos(Set.copyOf(roles))).willReturn(responses);

        List<RoleResponse> result = roleService.find("ADMIN");

        assertThat(result).containsExactly(roleResponse);
    }

    @Test
    @DisplayName("findCapabilityRolesBySystemRoleId - returns capability roles")
    void findCapabilityRolesBySystemRoleId() {
        RoleInheritance inheritance = RoleInheritance.builder()
                .childRole(role)
                .build();

        Set<RoleInheritance> inheritances = Set.of(inheritance);
        Set<Role> roles = Set.of(role);
        List<RoleResponse> responses = List.of(roleResponse);

        given(roleInheritanceRepository.findByParentRoleId(1L)).willReturn(inheritances);
        given(roleMapper.rolesToDtos(roles)).willReturn(responses);

        List<RoleResponse> result = roleService.findCapabilityRolesBySystemRoleId(1L);

        assertThat(result).containsExactly(roleResponse);
    }

    @Test
    @DisplayName("findByRoleTypeCode - returns roles by type")
    void findByRoleTypeCode() {
        Set<Role> roles = Set.of(role);
        List<RoleResponse> responses = List.of(roleResponse);

        given(roleRepository.findByRoleTypeCode(RoleTypeCode.CAPABILITY)).willReturn(roles);
        given(roleMapper.rolesToDtos(roles)).willReturn(responses);

        List<RoleResponse> result = roleService.findByRoleTypeCode(RoleTypeCode.CAPABILITY);

        assertThat(result).containsExactly(roleResponse);
    }

    @Test
    @DisplayName("findAllActive - returns all roles")
    void findAllActive() {
        List<Role> roles = List.of(role);
        Set<Role> rolesSet = new HashSet<>(roles);
        List<RoleResponse> responses = List.of(roleResponse);

        given(roleRepository.findAll()).willReturn(roles);
        given(roleMapper.rolesToDtos(rolesSet)).willReturn(responses);

        List<RoleResponse> result = roleService.findAllActive();

        assertThat(result).containsExactly(roleResponse);
    }

    @Test
    @DisplayName("getRoles - returns roles for user")
    void getRoles() {
        UUID userId = UUID.randomUUID();
        User user = User.builder().build();

        UserRole userRole = UserRole.builder()
                .user(user)
                .role(role)
                .build();

        user.setUserRoles(Set.of(userRole));

        List<RoleResponse> responses = List.of(roleResponse);

        given(userService.getUser(userId)).willReturn(user);
        given(roleMapper.rolesToDtos(user.getRoles())).willReturn(responses);

        List<RoleResponse> result = roleService.getRoles(userId);

        assertThat(result).containsExactly(roleResponse);
    }

    @Test
    @DisplayName("getAllRoles - returns functional and assignment roles")
    void getAllRoles() {
        UUID userId = UUID.randomUUID();

        RoleAssignmentResponse assignmentResponse = RoleAssignmentResponse.builder()
                .code("ADMIN")
                .build();

        FunctionalRoleResponse functionalResponse = FunctionalRoleResponse.builder()
                .code("ADMIN")
                .build();

        RoleInheritance inheritance = RoleInheritance
                .builder()
                .childRole(role)
                .build();
        role.setRoleInheritances(Set.of(inheritance));

        User user = User.builder().build();

        UserRole userRole = UserRole.builder()
                .user(user)
                .role(role)
                .build();

        user.setUserRoles(Set.of(userRole));

        given(userService.getUser(userId)).willReturn(user);
        given(roleMapper.rolesToFunctionalDtos(anySet())).willReturn(Set.of(functionalResponse));
        given(roleMapper.inheritancesToDtos(anySet())).willReturn(Set.of(assignmentResponse));

        AllRolesResponse result = roleService.getAllRoles(userId);

        assertThat(result).isNotNull();
        assertThat(result.getRoles()).containsExactly(functionalResponse);
        assertThat(result.getRoleAssignments()).containsExactly(assignmentResponse);
    }

    @Test
    @DisplayName("getInheritedRoles - returns flat list of all inherited roles")
    void getInheritedRoles() {
        Role child = Role.builder()
                .roleName("CHILD")
                .build();

        Role parent = Role.builder()
                .roleName("PARENT")
                .build();

        RoleInheritance inheritance = RoleInheritance
                .builder()
                .childRole(child)
                .build();

        parent.setRoleInheritances(Set.of(inheritance));
        child.setRoleInheritances(Collections.emptySet());

        Stream<Role> result = roleService.getInheritedRoles(Set.of(parent));

        List<Role> resultList = result.toList();
        assertThat(resultList).containsExactlyInAnyOrder(parent);
    }
}

