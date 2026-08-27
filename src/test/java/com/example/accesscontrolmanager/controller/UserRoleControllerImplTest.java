package com.example.accesscontrolmanager.controller;

import com.example.accesscontrolmanager.model.UserRoleAuditRecord;
import com.example.accesscontrolmanager.model.UserRoleDto;
import com.example.accesscontrolmanager.service.UserRoleService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

import static org.hamcrest.Matchers.hasSize;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(UserRoleControllerImpl.class)
@AutoConfigureMockMvc(addFilters = false)
@DisplayName("UserRoleControllerImpl Unit Tests")
class UserRoleControllerImplTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private UserRoleService userRoleService;

    @Autowired
    private ObjectMapper objectMapper;

    private UUID systemUserId;

    @BeforeEach
    void setUp() {
        systemUserId = UUID.randomUUID();
    }

    @Test
    @DisplayName("GET /v1/userRoles/{systemUserId}/roles - returns list of user roles")
    void getByUser_success() throws Exception {
        UserRoleDto dto = UserRoleDto.builder().build();
        when(userRoleService.getByUser(eq(systemUserId))).thenReturn(List.of(dto));

        mockMvc.perform(get("/v1/userRoles/{systemUserId}/roles", systemUserId)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)));
    }

    @Test
    @DisplayName("GET /v1/userRoles/{systemUserId}/roles/history - returns audit history")
    void getHistory_success() throws Exception {
        UserRoleAuditRecord record = UserRoleAuditRecord.builder()
                .userRoleId(1L)
                .roleName("ADMIN")
                .roleTypeCode("PERMISSION")
                .validFrom(Instant.parse("2025-01-01T00:00:00Z"))
                .assignedByName("System Admin")
                .build();

        when(userRoleService.getHistory(eq(systemUserId))).thenReturn(List.of(record));

        mockMvc.perform(get("/v1/userRoles/{systemUserId}/roles/history", systemUserId)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].roleName").value("ADMIN"))
                .andExpect(jsonPath("$[0].roleTypeCode").value("PERMISSION"))
                .andExpect(jsonPath("$[0].assignedByName").value("System Admin"));
    }

    @Test
    @DisplayName("GET /v1/userRoles/{systemUserId}/roles/history - returns empty array when no history")
    void getHistory_empty() throws Exception {
        when(userRoleService.getHistory(eq(systemUserId))).thenReturn(List.of());

        mockMvc.perform(get("/v1/userRoles/{systemUserId}/roles/history", systemUserId)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(0)));
    }
}
