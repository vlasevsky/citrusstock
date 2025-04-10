package com.citrusmall.citrusstock.controller;

import com.citrusmall.citrusstock.dto.PermissionDTO;
import com.citrusmall.citrusstock.dto.RoleDTO;
import com.citrusmall.citrusstock.dto.RolePermissionsDTO;
import com.citrusmall.citrusstock.mapper.PermissionMapper;
import com.citrusmall.citrusstock.mapper.RoleMapper;
import com.citrusmall.citrusstock.model.Role;
import com.citrusmall.citrusstock.service.PermissionService;
import com.citrusmall.citrusstock.service.RoleService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/warehouse/roles")
@RequiredArgsConstructor
public class RoleController {

    private final RoleService roleService;
    private final PermissionService permissionService;
    private final RoleMapper roleMapper;
    private final PermissionMapper permissionMapper;

    @GetMapping("/permissions")
    @PreAuthorize("hasAuthority('admin:manage_roles')")
    public ResponseEntity<List<PermissionDTO>> getAllPermissions() {
        List<PermissionDTO> permissions = permissionService.getAllPermissions().stream()
                .map(permissionMapper::toPermissionDTO)
                .collect(Collectors.toList());
        return ResponseEntity.ok(permissions);
    }

    @GetMapping
    @PreAuthorize("hasAuthority('admin:manage_roles')")
    public ResponseEntity<List<RoleDTO>> getAllRoles() {
        List<RoleDTO> roles = roleService.getAllRoles().stream()
                .map(roleMapper::toRoleDTO)
                .collect(Collectors.toList());
        return ResponseEntity.ok(roles);
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAuthority('admin:manage_roles')")
    public ResponseEntity<RoleDTO> getRoleById(@PathVariable Long id) {
        Role role = roleService.getRoleById(id);
        return ResponseEntity.ok(roleMapper.toRoleDTO(role));
    }

    @PostMapping
    @PreAuthorize("hasAuthority('admin:manage_roles')")
    public ResponseEntity<RoleDTO> createRole(@Valid @RequestBody RoleDTO roleDTO) {
        Role role = roleMapper.toRole(roleDTO);
        Role savedRole = roleService.createRole(role);
        return ResponseEntity.ok(roleMapper.toRoleDTO(savedRole));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAuthority('admin:manage_roles')")
    public ResponseEntity<RoleDTO> updateRole(
            @PathVariable Long id,
            @Valid @RequestBody RoleDTO roleDTO) {
        Role role = roleMapper.toRole(roleDTO);
        Role updatedRole = roleService.updateRole(id, role);
        return ResponseEntity.ok(roleMapper.toRoleDTO(updatedRole));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAuthority('admin:manage_roles')")
    public ResponseEntity<Void> deleteRole(@PathVariable Long id) {
        roleService.deleteRole(id);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/{roleId}/permissions")
    @PreAuthorize("hasAuthority('admin:manage_roles')")
    public ResponseEntity<RoleDTO> setPermissionsToRole(
            @PathVariable Long roleId,
            @Valid @RequestBody RolePermissionsDTO permissionsDTO) {
        Set<Long> permissionIds = new HashSet<>(permissionsDTO.getPermissionIds());
        Role updatedRole = roleService.setPermissionsToRole(roleId, permissionIds);
        return ResponseEntity.ok(roleMapper.toRoleDTO(updatedRole));
    }

    @PostMapping("/{roleId}/permissions/{permissionId}")
    @PreAuthorize("hasAuthority('admin:manage_roles')")
    public ResponseEntity<RoleDTO> addPermissionToRole(
            @PathVariable Long roleId,
            @PathVariable Long permissionId) {
        Role updatedRole = roleService.addPermissionToRole(roleId, permissionId);
        return ResponseEntity.ok(roleMapper.toRoleDTO(updatedRole));
    }

    @DeleteMapping("/{roleId}/permissions/{permissionId}")
    @PreAuthorize("hasAuthority('admin:manage_roles')")
    public ResponseEntity<RoleDTO> removePermissionFromRole(
            @PathVariable Long roleId,
            @PathVariable Long permissionId) {
        Role updatedRole = roleService.removePermissionFromRole(roleId, permissionId);
        return ResponseEntity.ok(roleMapper.toRoleDTO(updatedRole));
    }
} 