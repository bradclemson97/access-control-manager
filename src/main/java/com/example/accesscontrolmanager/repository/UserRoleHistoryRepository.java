package com.example.accesscontrolmanager.repository;

import com.example.accesscontrolmanager.model.UserRoleAuditRecord;
import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.sql.Timestamp;
import java.util.List;
import java.util.UUID;

@Repository
@RequiredArgsConstructor
public class UserRoleHistoryRepository {

    private final JdbcTemplate jdbcTemplate;

    public List<UserRoleAuditRecord> findBySystemUserId(UUID systemUserId) {
        return jdbcTemplate.query("""
                SELECT
                    urh.ure_id,
                    r.role_name,
                    r.role_type_code,
                    lower(urh.sys_period)  AS valid_from,
                    upper(urh.sys_period)  AS valid_to,
                    urh.created_by,
                    urh.created_date,
                    COALESCE(ud.first_name || ' ' || ud.last_name, urh.created_by::text) AS assigned_by_name
                FROM access_control.user_roles_history urh
                JOIN access_control.roles r ON r.roe_id = urh.roe_id
                LEFT JOIN user_management.users umu ON umu.system_user_id = urh.created_by
                LEFT JOIN user_management.user_details ud ON ud.usr_id = umu.usr_id
                    AND ud.known_to_date IS NULL
                WHERE urh.usr_id = (
                    SELECT usr_id FROM access_control.users WHERE system_user_id = ?
                )
                UNION ALL
                SELECT
                    ur.ure_id,
                    r.role_name,
                    r.role_type_code,
                    lower(ur.sys_period)      AS valid_from,
                    NULL::timestamptz         AS valid_to,
                    ur.created_by,
                    ur.created_date,
                    COALESCE(ud.first_name || ' ' || ud.last_name, ur.created_by::text) AS assigned_by_name
                FROM access_control.user_roles ur
                JOIN access_control.roles r ON r.roe_id = ur.roe_id
                LEFT JOIN user_management.users umu ON umu.system_user_id = ur.created_by
                LEFT JOIN user_management.user_details ud ON ud.usr_id = umu.usr_id
                    AND ud.known_to_date IS NULL
                WHERE ur.usr_id = (
                    SELECT usr_id FROM access_control.users WHERE system_user_id = ?
                )
                ORDER BY valid_from DESC
                """,
                (rs, i) -> {
                    Timestamp validTo = rs.getTimestamp("valid_to");
                    String assignedByStr = rs.getString("created_by");
                    Timestamp assignedAt = rs.getTimestamp("created_date");
                    return UserRoleAuditRecord.builder()
                            .userRoleId(rs.getLong("ure_id"))
                            .roleName(rs.getString("role_name"))
                            .roleTypeCode(rs.getString("role_type_code"))
                            .validFrom(rs.getTimestamp("valid_from").toInstant())
                            .validTo(validTo != null ? validTo.toInstant() : null)
                            .assignedBy(assignedByStr != null ? UUID.fromString(assignedByStr) : null)
                            .assignedByName(rs.getString("assigned_by_name"))
                            .assignedAt(assignedAt != null ? assignedAt.toInstant() : null)
                            .build();
                },
                systemUserId, systemUserId);
    }
}
