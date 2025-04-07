package com.citrusmall.citrusstock.mapper;

import com.citrusmall.citrusstock.dto.UserCreateRequest;
import com.citrusmall.citrusstock.dto.UserResponse;
import com.citrusmall.citrusstock.model.User;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface UserMapper {
    @Mapping(target = "enabled", constant = "true")
    User toUser(UserCreateRequest request);

    UserResponse toUserResponse(User user);
}
