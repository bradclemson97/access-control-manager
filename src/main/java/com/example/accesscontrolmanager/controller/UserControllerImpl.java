package com.example.accesscontrolmanager.controller;

import com.example.accesscontrolmanager.controller.request.CreateUserRequest;
import com.example.accesscontrolmanager.controller.response.CreateUserResponse;
import com.example.accesscontrolmanager.model.UserDto;
import com.example.accesscontrolmanager.service.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

/**
 * Implementation of APIs for retrieving and managing users.
 */
@RestController
@RequiredArgsConstructor
@Slf4j
public class UserControllerImpl implements UserController {

    private final UserService userService;

    @Override
    public CreateUserResponse createUser(CreateUserRequest request) {
        log.info("Attempting to create user {} ",
                request.getSystemUserId());
        CreateUserResponse response = userService.createUser(request);
        log.info("Successfully created user with systemUserId of {} ",
                request.getSystemUserId());
        return response;
    }

    @Override
    public UserDto getUser(UUID systemUserId) {
        log.info("Attempting to retrieve the user {}",
                systemUserId);
        UserDto user = userService.getUserInfo(systemUserId);
        log.info("Successfully retrieved the user {}",
                systemUserId);
        return user;
    }

    @Override
    public UserDto getCurrentUser(UUID systemUserId) {
        log.info("Attempting to retrieve the current user {}",
                systemUserId);
        UserDto user = userService.getUserInfo(systemUserId);
        log.info("Successfully retrieved the current user {}",
                systemUserId);
        return user;
    }

}
