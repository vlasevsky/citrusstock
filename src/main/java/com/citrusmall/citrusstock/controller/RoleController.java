package com.citrusmall.citrusstock.controller;

import com.citrusmall.citrusstock.dto.RoleTableDTO;
import com.citrusmall.citrusstock.service.RoleService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Set;

@RestController
@RequestMapping("/api/warehouse/roles")
@RequiredArgsConstructor
public class RoleController {

    private final RoleService roleService;

    @GetMapping
    @PreAuthorize("hasAuthority('admin:manage_roles')")
    public ResponseEntity<List<RoleTableDTO>> getAllRoles() {
        return ResponseEntity.ok(roleService.getAllRolesForTable());
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAuthority('admin:manage_roles')")
    public ResponseEntity<RoleTableDTO> getRoleById(@PathVariable Long id) {
        return ResponseEntity.ok(roleService.getRoleTableDTOById(id));
    }

    @PostMapping
    @PreAuthorize("hasAuthority('admin:manage_roles')")
    public ResponseEntity<RoleTableDTO> createRole(@RequestBody RoleTableDTO roleDTO) {
        return ResponseEntity.ok(roleService.createRoleFromDTO(roleDTO));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAuthority('admin:manage_roles')")
    public ResponseEntity<RoleTableDTO> updateRole(
            @PathVariable Long id,
            @RequestBody RoleTableDTO roleDTO) {
        return ResponseEntity.ok(roleService.updateRoleFromDTO(id, roleDTO));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAuthority('admin:manage_roles')")
    public ResponseEntity<Void> deleteRole(@PathVariable Long id) {
        roleService.deleteRole(id);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/{roleId}/permissions")
    @PreAuthorize("hasAuthority('admin:manage_roles')")
    public ResponseEntity<Void> updateRolePermissions(
            @PathVariable Long roleId,
            @RequestBody Set<Long> permissionIds) {
        roleService.setPermissionsToRole(roleId, permissionIds);
        return ResponseEntity.ok().build();
    }
} 