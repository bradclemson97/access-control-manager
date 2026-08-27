package com.example.accesscontrolmanager.service;

import com.example.accesscontrolmanager.client.KmClient;
import com.example.accesscontrolmanager.controller.request.UserRoleRequest;
import com.example.accesscontrolmanager.domain.Role;
import com.example.accesscontrolmanager.domain.RoleInheritance;
import com.example.accesscontrolmanager.domain.User;
import com.example.accesscontrolmanager.domain.UserRole;
import com.example.accesscontrolmanager.domain.enums.InheritanceType;
import com.example.accesscontrolmanager.domain.enums.RoleTypeCode;
import com.example.accesscontrolmanager.exception.RoleAssignmentNotAllowedException;
import com.example.accesscontrolmanager.mapper.UserRoleMapper;
import com.example.accesscontrolmanager.model.UserRoleAuditRecord;
import com.example.accesscontrolmanager.model.UserRoleDto;
import com.example.accesscontrolmanager.repository.UserRoleHistoryRepository;
import com.example.accesscontrolmanager.repository.UserRoleRepository;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.BDDMockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("UserRoleServiceImpl Unit Tests")
class UserRoleServiceImplTest {

    @Mock
    private UserRoleRepository userRoleRepository;

    @Mock
    private UserRoleHistoryRepository userRoleHistoryRepository;

    @Mock
    private UserRoleMapper userRoleMapper;

    @Mock
    private UserService userService;

    @Mock
    private RoleService roleService;

    @Mock
    private KmClient kmClient;

    @InjectMocks
    private UserRoleServiceImpl userRoleService;

    private UUID systemUserId;
    private UUID assignerUserId;
    private Role capabilityRole;
    private User assigner;
    private User assignee;
    private UserRole existingUserRole;
    private UserRoleDto userRoleDto;

    @BeforeEach
    void setUp() {
        systemUserId = UUID.randomUUID();
        assignerUserId = UUID.randomUUID();

        capabilityRole = Role.builder()
                .id(2L)
                .roleName("CREATE_USERS")
                .roleTypeCode(RoleTypeCode.CAPABILITY)
                .build();

        RoleInheritance assignment = RoleInheritance.builder()
                .inheritanceType(InheritanceType.ASSIGNMENT)
                .childRole(capabilityRole)
                .build();

        Role permissionRole = Role.builder()
                .id(1L)
                .roleName("ADMIN")
                .roleTypeCode(RoleTypeCode.PERMISSION)
                .roleInheritances(Set.of(assignment))
                .build();

        UserRole assignerRole = UserRole.builder()
                .id(10L)
                .role(permissionRole)
                .build();

        assigner = User.builder()
                .id(100L)
                .systemUserId(assignerUserId)
                .userRoles(new HashSet<>(Set.of(assignerRole)))
                .build();

        assignee = User.builder()
                .id(200L)
                .systemUserId(systemUserId)
                .userRoles(new HashSet<>())
                .build();

        existingUserRole = UserRole.builder()
                .id(20L)
                .user(assignee)
                .role(capabilityRole)
                .build();

        userRoleDto = UserRoleDto.builder().build();
    }

    @Test
    @DisplayName("get(id) - returns UserRoleDto when found")
    void get_success() {
        given(userRoleRepository.findById(20L)).willReturn(Optional.of(existingUserRole));
        given(userRoleMapper.map(existingUserRole)).willReturn(userRoleDto);

        UserRoleDto result = userRoleService.get(20L);

        assertThat(result).isEqualTo(userRoleDto);
    }

    @Test
    @DisplayName("get(id) - throws EntityNotFoundException when not found")
    void get_notFound() {
        given(userRoleRepository.findById(99L)).willReturn(Optional.empty());

        assertThatThrownBy(() -> userRoleService.get(99L))
                .isInstanceOf(EntityNotFoundException.class);
    }

    @Test
    @DisplayName("getByUser - returns all user roles mapped to DTOs")
    void getByUser_success() {
        assignee.getUserRoles().add(existingUserRole);
        given(userService.getUser(systemUserId)).willReturn(assignee);
        given(userRoleMapper.map(existingUserRole)).willReturn(userRoleDto);

        List<UserRoleDto> result = userRoleService.getByUser(systemUserId);

        assertThat(result).containsExactly(userRoleDto);
    }

    @Test
    @DisplayName("getByIdAndUser - returns matching UserRoleDto")
    void getByIdAndUser_success() {
        assignee.getUserRoles().add(existingUserRole);
        given(userService.getUser(systemUserId)).willReturn(assignee);
        given(userRoleMapper.map(existingUserRole)).willReturn(userRoleDto);

        UserRoleDto result = userRoleService.getByIdAndUser(20L, systemUserId);

        assertThat(result).isEqualTo(userRoleDto);
    }

    @Test
    @DisplayName("getByIdAndUser - throws EntityNotFoundException when role ID not found for user")
    void getByIdAndUser_notFound() {
        given(userService.getUser(systemUserId)).willReturn(assignee);

        assertThatThrownBy(() -> userRoleService.getByIdAndUser(99L, systemUserId))
                .isInstanceOf(EntityNotFoundException.class);
    }

    @Test
    @DisplayName("save - assigns new role when assigner is permitted to assign it")
    void save_success() {
        UserRoleRequest request = UserRoleRequest.builder().roleId(2L).build();
        UserRole newUserRole = UserRole.builder().id(30L).user(assignee).role(capabilityRole).build();

        given(userService.getUser(assignerUserId)).willReturn(assigner);
        given(userService.getUser(systemUserId)).willReturn(assignee);
        given(userRoleMapper.map(assignee, request)).willReturn(newUserRole);
        given(roleService.getInheritedRoles(anySet())).willReturn(Stream.empty());
        given(userRoleMapper.map(newUserRole)).willReturn(userRoleDto);

        List<UserRoleDto> result = userRoleService.save(systemUserId, assignerUserId, List.of(request));

        assertThat(result).containsExactly(userRoleDto);
    }

    @Test
    @DisplayName("save - throws RoleAssignmentNotAllowedException when new role is not in assigner's permitted set")
    void save_throwsWhenNewRoleNotAssignable() {
        Role forbiddenRole = Role.builder().id(99L).roleName("FORBIDDEN").build();
        UserRoleRequest request = UserRoleRequest.builder().roleId(99L).build();
        UserRole mappedRole = UserRole.builder().user(assignee).role(forbiddenRole).build();

        given(userService.getUser(assignerUserId)).willReturn(assigner);
        given(userService.getUser(systemUserId)).willReturn(assignee);
        given(userRoleMapper.map(assignee, request)).willReturn(mappedRole);

        assertThatThrownBy(() -> userRoleService.save(systemUserId, assignerUserId, List.of(request)))
                .isInstanceOf(RoleAssignmentNotAllowedException.class)
                .hasMessageContaining("New role cannot be assigned");
    }

    @Test
    @DisplayName("save - throws RoleAssignmentNotAllowedException when removing a role the assigner cannot manage")
    void save_throwsWhenRemovingNonAssignableRole() {
        Role forbiddenRole = Role.builder().id(99L).roleName("FORBIDDEN").build();
        UserRole existingForbidden = UserRole.builder().id(50L).user(assignee).role(forbiddenRole).build();
        assignee.getUserRoles().add(existingForbidden);

        UserRoleRequest request = UserRoleRequest.builder().roleId(2L).build();
        UserRole mappedNewRole = UserRole.builder().user(assignee).role(capabilityRole).build();

        given(userService.getUser(assignerUserId)).willReturn(assigner);
        given(userService.getUser(systemUserId)).willReturn(assignee);
        given(userRoleMapper.map(assignee, request)).willReturn(mappedNewRole);

        assertThatThrownBy(() -> userRoleService.save(systemUserId, assignerUserId, List.of(request)))
                .isInstanceOf(RoleAssignmentNotAllowedException.class)
                .hasMessageContaining("Existing role cannot be removed");
    }

    @Test
    @DisplayName("getHistory - returns audit records from repository")
    void getHistory_returnsAuditRecords() {
        UserRoleAuditRecord record = UserRoleAuditRecord.builder()
                .userRoleId(1L)
                .roleName("ADMIN")
                .roleTypeCode("PERMISSION")
                .validFrom(java.time.Instant.now())
                .build();
        given(userRoleHistoryRepository.findBySystemUserId(systemUserId)).willReturn(List.of(record));

        List<UserRoleAuditRecord> result = userRoleService.getHistory(systemUserId);

        assertThat(result).containsExactly(record);
        then(userRoleHistoryRepository).should().findBySystemUserId(systemUserId);
    }

    @Test
    @DisplayName("getHistory - returns empty list when no history exists")
    void getHistory_returnsEmptyList() {
        given(userRoleHistoryRepository.findBySystemUserId(systemUserId)).willReturn(List.of());

        List<UserRoleAuditRecord> result = userRoleService.getHistory(systemUserId);

        assertThat(result).isEmpty();
    }
}
