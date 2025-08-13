package com.example.accesscontrolmanager.mapper;

import static org.assertj.core.api.Assertions.assertThat;

import com.example.accesscontrolmanager.controller.response.FunctionalRoleResponse;
import com.example.accesscontrolmanager.controller.response.RoleAssignmentResponse;
import com.example.accesscontrolmanager.controller.response.RoleResponse;
import com.example.accesscontrolmanager.domain.Role;
import com.example.accesscontrolmanager.domain.RoleInheritance;
import com.example.accesscontrolmanager.domain.enums.RoleTypeCode;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mapstruct.factory.Mappers;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.List;
import java.util.Set;

@ExtendWith(MockitoExtension.class)
@DisplayName("RoleMapper Unit Tests")
class RoleMapperTest {

    private final RoleMapper mapper = Mappers.getMapper(RoleMapper.class);

    @Mock
    private RolePrivilegeMapper rolePrivilegeMapper;

    @Mock
    private RoleInheritanceMapper roleInheritanceMapper;

    @BeforeEach
    void setUp() {
        ReflectionTestUtils.setField(mapper, "rolePrivilegeMapper", rolePrivilegeMapper);
        ReflectionTestUtils.setField(mapper, "roleInheritanceMapper", roleInheritanceMapper);
    }

    @Test
    @DisplayName("roleToDto - maps correctly")
    void roleToDto_mapsCorrectly() {
        // Given
        Role role = Role.builder()
                .id(1L)
                .roleName("ADMIN")
                .roleTypeCode(RoleTypeCode.CAPABILITY)
                .build();

        // When
        RoleResponse result = mapper.roleToDto(role);

        // Then
        assertThat(result).isNotNull();
        assertThat(result.getId()).isEqualTo(role.getId());
        assertThat(result.getRoleName()).isEqualTo(role.getRoleName());
        assertThat(result.getRoleTypeCode()).isEqualTo(role.getRoleTypeCode());
    }

    @Test
    @DisplayName("rolesToDtos - maps set of roles to list of RoleResponses")
    void rolesToDtos_mapsCorrectly() {
        // Given
        Role role1 = Role.builder()
                .id(1L)
                .roleName("ADMIN")
                .build();

        Role role2 = Role.builder()
                .id(2L)
                .roleName("USER")
                .build();

        Set<Role> roles = Set.of(role1, role2);

        // When
        List<RoleResponse> result = mapper.rolesToDtos(roles);

        // Then
        assertThat(result).hasSize(2);
        assertThat(result).extracting(RoleResponse::getRoleName)
                .containsExactlyInAnyOrder("ADMIN", "USER");
    }

    @Test
    @DisplayName("roleToFunctionalDto - maps correctly")
    void roleToFunctionalDto_mapsCorrectly() {
        // Given
        Role role = Role.builder()
                .roleName("VIEWER")
                .roleTypeCode(RoleTypeCode.PERMISSION)
                .build();

        // When
        FunctionalRoleResponse result = mapper.roleToFunctionalDto(role);

        // Then
        assertThat(result).isNotNull();
        assertThat(result.getCode()).isEqualTo("VIEWER");
        assertThat(result.getTypeCode()).isEqualTo(RoleTypeCode.PERMISSION);
    }

    @Test
    @DisplayName("rolesToFunctionalDtos - maps set of roles to set of FunctionalRoleResponses")
    void rolesToFunctionalDtos_mapsCorrectly() {
        // Given
        Role role1 = Role.builder()
                .roleName("VIEWER")
                .roleTypeCode(RoleTypeCode.CAPABILITY)
                .build();

        Role role2 = Role.builder()
                .roleName("ADMIN")
                .roleTypeCode(RoleTypeCode.CAPABILITY)
                .build();

        Set<Role> roles = Set.of(role1, role2);

        // When
        Set<FunctionalRoleResponse> result = mapper.rolesToFunctionalDtos(roles);

        // Then
        assertThat(result).hasSize(2);
        assertThat(result).extracting(FunctionalRoleResponse::getCode)
                .containsExactlyInAnyOrder("VIEWER", "ADMIN");
    }

    @Test
    @DisplayName("inheritanceToDto - maps correctly")
    void inheritanceToDto_mapsCorrectly() {
        // Given
        Role childRole = Role.builder()
                .roleName("CHILD_ROLE")
                .build();

        RoleInheritance inheritance = RoleInheritance.builder()
                .childRole(childRole)
                .build();

        // When
        RoleAssignmentResponse result = mapper.inheritanceToDto(inheritance);

        // Then
        assertThat(result).isNotNull();
        assertThat(result.getCode()).isEqualTo("CHILD_ROLE");
    }

    @Test
    @DisplayName("inheritancesToDtos - maps set of RoleInheritances to set of RoleAssignmentResponses")
    void inheritancesToDtos_mapsCorrectly() {
        // Given
        RoleInheritance i1 = RoleInheritance.builder().build();
        Role r1 = Role.builder()
                .roleName("R1")
                .build();
        i1.setChildRole(r1);

        RoleInheritance i2 = RoleInheritance.builder().build();
        Role r2 = Role.builder()
                .roleName("R2")
                .build();
        i2.setChildRole(r2);

        Set<RoleInheritance> inheritances = Set.of(i1, i2);

        // When
        Set<RoleAssignmentResponse> result = mapper.inheritancesToDtos(inheritances);

        // Then
        assertThat(result).hasSize(2);
        assertThat(result).extracting(RoleAssignmentResponse::getCode)
                .containsExactlyInAnyOrder("R1", "R2");
    }

    @Test
    @DisplayName("roleToDto - maps null to null")
    void roleToDto_null() {
        // When
        RoleResponse result = mapper.roleToDto(null);

        // Then
        assertThat(result).isNull();
    }

    @Test
    @DisplayName("rolesToDtos - maps null to empty list")
    void rolesToDtos_null() {
        // When
        List<RoleResponse> result = mapper.rolesToDtos(null);

        // Then
        assertThat(result).isEmpty();
    }
}
