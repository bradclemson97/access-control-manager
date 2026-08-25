package com.example.accesscontrolmanager.service;

import com.example.accesscontrolmanager.controller.request.CreateUserRequest;
import com.example.accesscontrolmanager.controller.response.CreateUserResponse;
import com.example.accesscontrolmanager.domain.User;
import com.example.accesscontrolmanager.domain.enums.YesNo;
import com.example.accesscontrolmanager.exception.ConflictException;
import com.example.accesscontrolmanager.mapper.UserMapper;
import com.example.accesscontrolmanager.model.UserDto;
import com.example.accesscontrolmanager.repository.UserRepository;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.BDDMockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("UserServiceImpl Unit Tests")
class UserServiceImplTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private UserMapper userMapper;

    @InjectMocks
    private UserServiceImpl userService;

    private UUID systemUserId;
    private User user;

    @BeforeEach
    void setUp() {
        systemUserId = UUID.randomUUID();
        user = User.builder()
                .id(1L)
                .systemUserId(systemUserId)
                .build();
    }

    @Test
    @DisplayName("createUser - persists user and returns systemUserId")
    void createUser_success() {
        CreateUserRequest request = CreateUserRequest.builder()
                .systemUserId(systemUserId)
                .build();

        given(userRepository.existsBySystemUserId(systemUserId)).willReturn(false);
        given(userMapper.requestToUser(request)).willReturn(user);
        given(userRepository.save(user)).willReturn(user);

        CreateUserResponse response = userService.createUser(request);

        assertThat(response.getSystemUserId()).isEqualTo(systemUserId);
        then(userRepository).should().save(user);
    }

    @Test
    @DisplayName("createUser - throws ConflictException when systemUserId already exists")
    void createUser_conflict() {
        CreateUserRequest request = CreateUserRequest.builder()
                .systemUserId(systemUserId)
                .build();

        given(userRepository.existsBySystemUserId(systemUserId)).willReturn(true);

        assertThatThrownBy(() -> userService.createUser(request))
                .isInstanceOf(ConflictException.class)
                .hasMessageContaining("SystemUserId already exists");
    }

    @Test
    @DisplayName("getUser - returns User when found")
    void getUser_success() {
        given(userRepository.findBySystemUserId(systemUserId)).willReturn(Optional.of(user));

        User result = userService.getUser(systemUserId);

        assertThat(result).isEqualTo(user);
    }

    @Test
    @DisplayName("getUser - throws EntityNotFoundException when not found")
    void getUser_notFound() {
        given(userRepository.findBySystemUserId(systemUserId)).willReturn(Optional.empty());

        assertThatThrownBy(() -> userService.getUser(systemUserId))
                .isInstanceOf(EntityNotFoundException.class);
    }

    @Test
    @DisplayName("getUserInfo - returns UserDto for existing user")
    void getUserInfo_success() {
        UserDto userDto = UserDto.builder().systemUserId(systemUserId).build();

        given(userRepository.findBySystemUserId(systemUserId)).willReturn(Optional.of(user));
        given(userMapper.userToDto(user)).willReturn(userDto);

        UserDto result = userService.getUserInfo(systemUserId);

        assertThat(result.getSystemUserId()).isEqualTo(systemUserId);
    }

    @Test
    @DisplayName("lockUser - sets locked flag to YES and persists failed attempt count")
    void lockUser_success() {
        given(userRepository.findBySystemUserId(systemUserId)).willReturn(Optional.of(user));
        given(userRepository.save(user)).willReturn(user);

        userService.lockUser(systemUserId, 5);

        assertThat(user.getLocked()).isEqualTo(YesNo.YES);
        assertThat(user.getFailedLoginAttempts()).isEqualTo(5);
        then(userRepository).should().save(user);
    }

    @Test
    @DisplayName("lockUser - throws EntityNotFoundException when user not found")
    void lockUser_notFound() {
        given(userRepository.findBySystemUserId(systemUserId)).willReturn(Optional.empty());

        assertThatThrownBy(() -> userService.lockUser(systemUserId, 3))
                .isInstanceOf(EntityNotFoundException.class);
    }

    @Test
    @DisplayName("unlockUser - clears locked flag and resets failed attempt count")
    void unlockUser_success() {
        user.setLocked(YesNo.YES);
        user.setFailedLoginAttempts(3);

        given(userRepository.findBySystemUserId(systemUserId)).willReturn(Optional.of(user));
        given(userRepository.save(user)).willReturn(user);

        userService.unlockUser(systemUserId);

        assertThat(user.getLocked()).isEqualTo(YesNo.NO);
        assertThat(user.getFailedLoginAttempts()).isEqualTo(0);
        then(userRepository).should().save(user);
    }

    @Test
    @DisplayName("unlockUser - throws EntityNotFoundException when user not found")
    void unlockUser_notFound() {
        given(userRepository.findBySystemUserId(systemUserId)).willReturn(Optional.empty());

        assertThatThrownBy(() -> userService.unlockUser(systemUserId))
                .isInstanceOf(EntityNotFoundException.class);
    }
}
