package com.example.accesscontrolmanager.model;

import lombok.Builder;
import lombok.Value;

import java.time.Instant;
import java.util.UUID;

@Value
@Builder
public class UserRoleAuditRecord {
    Long userRoleId;
    String roleName;
    String roleTypeCode;
    Instant validFrom;
    Instant validTo;
    UUID assignedBy;
    String assignedByName;
    Instant assignedAt;
}
