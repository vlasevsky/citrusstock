package com.citrusmall.citrusstock.mapper;

import com.citrusmall.citrusstock.dto.UserCreateRequest;
import com.citrusmall.citrusstock.dto.UserResponse;
import com.citrusmall.citrusstock.model.User;
import com.citrusmall.citrusstock.service.RoleService;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;
import org.springframework.beans.factory.annotation.Autowired;

@Mapper(componentModel = "spring")
public abstract class UserMapper {
    
    @Autowired
    protected RoleService roleService;
    
    @Mapping(target = "role", source = "role.name")
    public abstract UserResponse toUserResponse(User user);
    
    @Mapping(target = "role", source = "role", qualifiedByName = "stringToRole")
    @Mapping(target = "refreshTokens", ignore = true)
    @Mapping(target = "accountNonExpired", constant = "true")
    @Mapping(target = "accountNonLocked", constant = "true")
    @Mapping(target = "credentialsNonExpired", constant = "true")
    @Mapping(target = "enabled", constant = "true")
    public abstract User toUser(UserCreateRequest request);
    
    @Named("stringToRole")
    protected com.citrusmall.citrusstock.model.Role stringToRole(String roleName) {
        return roleService.getRoleByName(roleName);
    }
}
