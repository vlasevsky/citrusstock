package com.citrusmall.citrusstock.mapper;

import com.citrusmall.citrusstock.dto.PermissionDTO;
import com.citrusmall.citrusstock.model.Permission;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface PermissionMapper {
    PermissionDTO toPermissionDTO(Permission permission);
    Permission toPermission(PermissionDTO permissionDTO);
} 