package com.example.accesscontrolmanager.mapper;

import com.example.accesscontrolmanager.controller.request.CreateUserRequest;
import com.example.accesscontrolmanager.domain.User;
import com.example.accesscontrolmanager.model.UserDto;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.time.OffsetDateTime;

import static org.mapstruct.NullValueMappingStrategy.RETURN_DEFAULT;
import static org.mapstruct.NullValuePropertyMappingStrategy.IGNORE;

@Mapper(componentModel = "spring",
nullValuePropertyMappingStrategy = IGNORE,
        nullValueIterableMappingStrategy = RETURN_DEFAULT,
        imports = {OffsetDateTime.class},
        uses = {RoleMapper.class})
public interface UserMapper {

    /**
     * map a request to an entity.
     *
     * @param request UserRequest
     * @return User
     */
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "locked", ignore = true)
    @Mapping(target = "userRoles", ignore = true)
    @Mapping(target = "createdBy", ignore = true)
    @Mapping(target = "createdDate", ignore = true)
    @Mapping(target = "modifiedBy", ignore = true)
    @Mapping(target = "modifiedDate", ignore = true)
    User requestToUser(CreateUserRequest request);

    /**
     * map a user to a user dto
     *
     * @param user User
     * @return UserDto
     */
    UserDto userToDto(User user);

}
