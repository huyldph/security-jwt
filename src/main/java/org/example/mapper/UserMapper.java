package org.example.mapper;

import org.example.dto.request.ApiResponse;
import org.example.dto.request.UserRequest;
import org.example.dto.response.UserResponse;
import org.example.entity.User;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

@Mapper(componentModel = "spring")
public interface UserMapper {
    User userRequestToUser(UserRequest userRequest);

    @Mapping(target = "code", source = "code")
    @Mapping(target = "message", source = "message")
    @Mapping(target = "data", source = "user")
    ApiResponse<User> userToApiResponse(User user, Integer code, String message);

    void updateUser(@MappingTarget User user, UserRequest userRequest);

    @Mapping(target = "id", ignore = true)
    UserResponse userToUserResponse(User user);
}
