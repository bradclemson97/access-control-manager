package com.example.accesscontrolmanager.controller;

import com.example.accesscontrolmanager.controller.response.AllRolesResponse;
import com.example.accesscontrolmanager.controller.response.RoleResponse;
import com.example.accesscontrolmanager.domain.enums.RoleTypeCode;
import com.example.accesscontrolmanager.service.RoleService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.UUID;

/**
 * Implementation of APIs for querying Roles.
 */
@RestController
@RequiredArgsConstructor
@Slf4j
public class RoleControllerImpl implements RoleController {

    private final RoleService roleService;

    @Override
    public List<RoleResponse> findAll(String name, RoleTypeCode typeCode) {
        if (name != null) {
            log.info("Finding roles by name: {}", name);
            return roleService.find(name);
        }
        if (typeCode != null) {
            log.info("Finding roles by typeCode: {}", typeCode);
            return roleService.findByRoleTypeCode(typeCode);
        }
        log.info("Finding all active roles");
        return roleService.findAllActive();
    }

    @Override
    public RoleResponse findById(Long id) {
        log.info("Finding role by id: {}", id);
        return roleService.find(id);
    }

    @Override
    public List<RoleResponse> findCapabilities(Long id) {
        log.info("Finding capability roles for system role id: {}", id);
        return roleService.findCapabilityRolesBySystemRoleId(id);
    }

    @Override
    public List<RoleResponse> getUserRoles(UUID systemUserId) {
        log.info("Getting direct roles for user: {}", systemUserId);
        return roleService.getRoles(systemUserId);
    }

    @Override
    public AllRolesResponse getAllUserRoles(UUID systemUserId) {
        log.info("Getting all roles (including inherited) for user: {}", systemUserId);
        return roleService.getAllRoles(systemUserId);
    }
}
