package com.citrusmall.citrusstock.mapper;

import com.citrusmall.citrusstock.dto.PermissionDTO;
import com.citrusmall.citrusstock.dto.RoleDTO;
import com.citrusmall.citrusstock.model.Permission;
import com.citrusmall.citrusstock.model.Role;
import com.citrusmall.citrusstock.service.PermissionService;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Mapper(componentModel = "spring", uses = {PermissionMapper.class})
public abstract class RoleMapper {
    
    @Autowired
    protected PermissionService permissionService;
    
    @Autowired
    protected PermissionMapper permissionMapper;
    
    @Mapping(target = "permissionIds", source = "permissions", qualifiedByName = "permissionsToIds")
    @Mapping(target = "permissions", expression = "java(mapPermissions(role.getPermissions()))")
    public abstract RoleDTO toRoleDTO(Role role);
    
    @Mapping(target = "permissions", source = "permissionIds", qualifiedByName = "idsToPermissions")
    public abstract Role toRole(RoleDTO roleDTO);
    
    @Named("permissionsToIds")
    protected Set<Long> permissionsToIds(Set<Permission> permissions) {
        if (permissions == null) {
            return new HashSet<>();
        }
        return permissions.stream()
                .map(Permission::getId)
                .collect(Collectors.toSet());
    }
    
    @Named("idsToPermissions")
    protected Set<Permission> idsToPermissions(Set<Long> permissionIds) {
        if (permissionIds == null) {
            return new HashSet<>();
        }
        
        Set<Permission> permissions = new HashSet<>();
        for (Long id : permissionIds) {
            try {
                Permission permission = permissionService.getPermissionById(id);
                permissions.add(permission);
            } catch (Exception e) {
                // Skip invalid permission IDs
            }
        }
        return permissions;
    }
    
    // Вспомогательный метод для преобразования Set<Permission> в List<PermissionDTO>
    protected List<PermissionDTO> mapPermissions(Set<Permission> permissions) {
        if (permissions == null) {
            return new ArrayList<>();
        }
        return permissions.stream()
                .map(permissionMapper::toPermissionDTO)
                .collect(Collectors.toList());
    }
} 