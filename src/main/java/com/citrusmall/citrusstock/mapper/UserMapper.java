package com.citrusmall.citrusstock.mapper;

import com.citrusmall.citrusstock.dto.UserCreateRequest;
import com.citrusmall.citrusstock.dto.UserResponse;
import com.citrusmall.citrusstock.model.User;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface UserMapper {
    UserResponse toUserResponse(User user);
    User toUser(UserCreateRequest request);
}
