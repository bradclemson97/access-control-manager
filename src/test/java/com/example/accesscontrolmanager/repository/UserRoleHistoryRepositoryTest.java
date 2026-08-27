package com.example.accesscontrolmanager.repository;

import com.example.accesscontrolmanager.model.UserRoleAuditRecord;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;

import java.sql.ResultSet;
import java.sql.Timestamp;
import java.time.Instant;
import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
@DisplayName("UserRoleHistoryRepository Unit Tests")
class UserRoleHistoryRepositoryTest {

    @Mock
    private JdbcTemplate jdbcTemplate;

    private UserRoleHistoryRepository repository;

    private final UUID systemUserId = UUID.randomUUID();

    @BeforeEach
    void setUp() {
        repository = new UserRoleHistoryRepository(jdbcTemplate);
    }

    @Test
    @DisplayName("findBySystemUserId - delegates to JdbcTemplate with systemUserId passed twice (UNION ALL)")
    void findBySystemUserId_delegatesToJdbcTemplate() {
        UserRoleAuditRecord record = UserRoleAuditRecord.builder()
                .userRoleId(1L)
                .roleName("ADMIN")
                .roleTypeCode("PERMISSION")
                .validFrom(Instant.now())
                .build();

        given(jdbcTemplate.query(any(String.class), any(RowMapper.class), eq(systemUserId), eq(systemUserId)))
                .willReturn(List.of(record));

        List<UserRoleAuditRecord> result = repository.findBySystemUserId(systemUserId);

        assertThat(result).hasSize(1);
        assertThat(result.get(0)).isEqualTo(record);
        verify(jdbcTemplate).query(any(String.class), any(RowMapper.class), eq(systemUserId), eq(systemUserId));
    }

    @Test
    @DisplayName("findBySystemUserId - returns empty list when no role history exists")
    void findBySystemUserId_returnsEmptyList() {
        given(jdbcTemplate.query(any(String.class), any(RowMapper.class), eq(systemUserId), eq(systemUserId)))
                .willReturn(List.of());

        List<UserRoleAuditRecord> result = repository.findBySystemUserId(systemUserId);

        assertThat(result).isEmpty();
    }

    @Test
    @DisplayName("RowMapper - maps ResultSet columns including null valid_to for active roles")
    @SuppressWarnings("unchecked")
    void rowMapper_mapsActiveRole() throws Exception {
        Instant now = Instant.now();
        UUID assignedBy = UUID.randomUUID();

        ResultSet rs = mock(ResultSet.class);
        given(rs.getLong("ure_id")).willReturn(42L);
        given(rs.getString("role_name")).willReturn("CREATE_USERS");
        given(rs.getString("role_type_code")).willReturn("CAPABILITY");
        given(rs.getTimestamp("valid_from")).willReturn(Timestamp.from(now));
        given(rs.getTimestamp("valid_to")).willReturn(null);
        given(rs.getString("created_by")).willReturn(assignedBy.toString());
        given(rs.getString("assigned_by_name")).willReturn("John Admin");
        given(rs.getTimestamp("created_date")).willReturn(Timestamp.from(now));

        ArgumentCaptor<RowMapper<UserRoleAuditRecord>> mapperCaptor = ArgumentCaptor.forClass(RowMapper.class);
        given(jdbcTemplate.query(any(String.class), mapperCaptor.capture(), eq(systemUserId), eq(systemUserId)))
                .willReturn(List.of());

        repository.findBySystemUserId(systemUserId);

        UserRoleAuditRecord mapped = mapperCaptor.getValue().mapRow(rs, 0);

        assertThat(mapped).isNotNull();
        assertThat(mapped.getUserRoleId()).isEqualTo(42L);
        assertThat(mapped.getRoleName()).isEqualTo("CREATE_USERS");
        assertThat(mapped.getRoleTypeCode()).isEqualTo("CAPABILITY");
        assertThat(mapped.getValidFrom()).isEqualTo(now);
        assertThat(mapped.getValidTo()).isNull();
        assertThat(mapped.getAssignedBy()).isEqualTo(assignedBy);
        assertThat(mapped.getAssignedByName()).isEqualTo("John Admin");
        assertThat(mapped.getAssignedAt()).isEqualTo(now);
    }

    @Test
    @DisplayName("RowMapper - maps valid_to for historical (removed) roles")
    @SuppressWarnings("unchecked")
    void rowMapper_mapsRemovedRole() throws Exception {
        Instant validFrom = Instant.now().minusSeconds(3600);
        Instant validTo = Instant.now();

        ResultSet rs = mock(ResultSet.class);
        given(rs.getLong("ure_id")).willReturn(10L);
        given(rs.getString("role_name")).willReturn("ADMIN");
        given(rs.getString("role_type_code")).willReturn("PERMISSION");
        given(rs.getTimestamp("valid_from")).willReturn(Timestamp.from(validFrom));
        given(rs.getTimestamp("valid_to")).willReturn(Timestamp.from(validTo));
        given(rs.getString("created_by")).willReturn(null);
        given(rs.getString("assigned_by_name")).willReturn(null);
        given(rs.getTimestamp("created_date")).willReturn(null);

        ArgumentCaptor<RowMapper<UserRoleAuditRecord>> mapperCaptor = ArgumentCaptor.forClass(RowMapper.class);
        given(jdbcTemplate.query(any(String.class), mapperCaptor.capture(), eq(systemUserId), eq(systemUserId)))
                .willReturn(List.of());

        repository.findBySystemUserId(systemUserId);

        UserRoleAuditRecord mapped = mapperCaptor.getValue().mapRow(rs, 0);

        assertThat(mapped.getValidTo()).isEqualTo(validTo);
        assertThat(mapped.getAssignedBy()).isNull();
        assertThat(mapped.getAssignedAt()).isNull();
    }
}
