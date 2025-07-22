package com.example.accesscontrolmanager.service;

import com.example.accesscontrolmanager.controller.request.CreateUserRequest;
import com.example.accesscontrolmanager.controller.response.CreateUserResponse;
import com.example.accesscontrolmanager.domain.User;
import com.example.accesscontrolmanager.exception.ConflictException;
import com.example.accesscontrolmanager.mapper.UserMapper;
import com.example.accesscontrolmanager.model.UserDto;
import com.example.accesscontrolmanager.repository.UserRepository;
import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.*;

/**
 * Implementation of User Service.
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final UserMapper userMapper;

    private static final String KEYWORD_DELIMITER = " ";

    /**
     * Creates a user with the specified fields.
     * Calls the Keycloak manager which uses UserRepresentation and
     * CredentialRepresentation to create password credential.
     *
     * @param request the CreateUserRequest with the fields for the new user
     * @return CreateUserResponse
     */
    @Override
    @Transactional
    public CreateUserResponse createUser(CreateUserRequest request) {
        if (userRepository.existsBySystemUserId(request.getSystemUserId())) {
            throw new ConflictException("SystemUserId already exists");
        }

        User user = userMapper.requestToUser(request);
        user = userRepository.save(user);

        return CreateUserResponse.builder()
                .systemUserId(user.getSystemUserId())
                .build();
    }

    @Override
    public User getUser(UUID systemUserId) {
        return userRepository.findBySystemUserId(systemUserId)
                .orElseThrow(() -> {
                    log.info("User not found for systemUserId {}", systemUserId);
                    return new EntityNotFoundException("User not found for systemUserId");
                });
    }

    @Override
    public UserDto getUserInfo(UUID systemUserId) {
        User user = getUser(systemUserId);
        return userMapper.userToDto(user);
    }

}
