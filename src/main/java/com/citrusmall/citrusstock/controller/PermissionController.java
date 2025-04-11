package com.citrusmall.citrusstock.controller;

import com.citrusmall.citrusstock.dto.PermissionTableDTO;
import com.citrusmall.citrusstock.service.PermissionService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/warehouse/permissions")
@RequiredArgsConstructor
public class PermissionController {

    private final PermissionService permissionService;

    @GetMapping
    @PreAuthorize("hasAuthority('admin:manage_roles')")
    public ResponseEntity<List<PermissionTableDTO>> getAllPermissions() {
        return ResponseEntity.ok(permissionService.getAllPermissionsForTable());
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAuthority('admin:manage_roles')")
    public ResponseEntity<PermissionTableDTO> updatePermission(
            @PathVariable Long id,
            @RequestBody PermissionTableDTO permissionDTO) {
        return ResponseEntity.ok(permissionService.updatePermission(id, permissionDTO));
    }
} 