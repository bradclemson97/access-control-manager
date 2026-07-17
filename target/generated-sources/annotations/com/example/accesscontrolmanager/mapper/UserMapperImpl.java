package com.example.accesscontrolmanager.mapper;

import com.example.accesscontrolmanager.controller.request.CreateUserRequest;
import com.example.accesscontrolmanager.domain.User;
import com.example.accesscontrolmanager.model.UserDto;
import java.time.OffsetDateTime;
import javax.annotation.processing.Generated;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Generated(
    value = "org.mapstruct.ap.MappingProcessor",
    comments = "version: 1.5.5.Final, compiler: javac, environment: Java 17.0.16 (Oracle Corporation)"
)
@Component
public class UserMapperImpl implements UserMapper {

    @Autowired
    private RoleMapper roleMapper;

    @Override
    public User requestToUser(CreateUserRequest request) {
        if ( request == null ) {
            return null;
        }

        User.UserBuilder<?, ?> user = User.builder();

        user.systemUserId( request.getSystemUserId() );

        return user.build();
    }

    @Override
    public UserDto userToDto(User user) {
        if ( user == null ) {
            return null;
        }

        UserDto.UserDtoBuilder userDto = UserDto.builder();

        userDto.systemUserId( user.getSystemUserId() );
        userDto.locked( user.getLocked() );
        userDto.permissions( roleMapper.rolesToFunctionalDtos( user.getPermissions() ) );

        return userDto.build();
    }
}
