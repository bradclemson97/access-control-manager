package com.example.accesscontrolmanager.service;

import com.example.accesscontrolmanager.controller.request.CreateUserRequest;
import com.example.accesscontrolmanager.controller.response.CreateUserResponse;
import com.example.accesscontrolmanager.domain.User;
import com.example.accesscontrolmanager.model.UserDto;

import java.util.UUID;

/**
 * Service layer of retrieving and managing Users.
 */
public interface UserService {

    /**
     * Creates a new user with the specified fields.
     *
     * @param request the DTO with the fields for the new user
     * @return the generated saga response.
     */
    CreateUserResponse createUser(CreateUserRequest request);

    /**
     * find the user by systemUserId.
     *
     * @param systemUserId the systemUserId of the user to find
     * @return the found user.
     */
    User getUser(UUID systemUserId);

    /**
     * get the user information by  systemUserId.
     *
     * @param systemUserId the systemUserId of the user
     * @return the user information.
     */
    UserDto getUserInfo(UUID systemUserId);

}
