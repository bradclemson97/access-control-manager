package com.example.accesscontrolmanager.controller;

import com.example.accesscontrolmanager.controller.request.UserRoleListRequest;
import com.example.accesscontrolmanager.model.UserRoleDto;
import com.example.accesscontrolmanager.service.UserRoleService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.UUID;

import static com.example.accesscontrolmanager.config.SystemConstant.API_USER_ROLE;
import static com.example.accesscontrolmanager.config.SystemConstant.API_VERSION;

/**
 * UserRoleController implementation
 */
@RestController
@RequiredArgsConstructor
@Slf4j
public class UserRoleControllerImpl implements UserRoleController {

    private final UserRoleService userRoleService;

    @Override
    public List<UserRoleDto> save(
            UUID systemUserId, UUID assignerUserId, UserRoleListRequest request) {
        log.info("POST " + API_VERSION + API_USER_ROLE + " " + systemUserId
        + " " + request.toString());
        return userRoleService.save(systemUserId, assignerUserId, request.getUserRoleRequestList());
    }

    @Override
    public UserRoleDto get(UUID systemUserId, Long id) {
        log.info("GET " + API_VERSION + API_USER_ROLE + " " + systemUserId + " " + id);
        return userRoleService.getByIdAndUser(id, systemUserId);
    }

    @Override
    public List<UserRoleDto> getByUser(UUID systemUserId) {
        log.info("GET " + API_VERSION + API_USER_ROLE + "/userId " + systemUserId);
        return userRoleService.getByUser(systemUserId);
    }
}
