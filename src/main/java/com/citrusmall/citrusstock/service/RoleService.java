package com.citrusmall.citrusstock.service;

import com.citrusmall.citrusstock.dto.RoleTableDTO;
import com.citrusmall.citrusstock.model.Permission;
import com.citrusmall.citrusstock.model.Role;
import com.citrusmall.citrusstock.repository.PermissionRepository;
import com.citrusmall.citrusstock.repository.RoleRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * Service for managing roles.
 */
@Service
@RequiredArgsConstructor
public class RoleService {
    
    private final RoleRepository roleRepository;
    private final PermissionRepository permissionRepository;
    
    /**
     * Get all roles.
     * 
     * @return List of all roles
     */
    @Transactional(readOnly = true)
    public List<Role> getAllRoles() {
        return roleRepository.findAll();
    }
    
    /**
     * Get a role by its ID.
     * 
     * @param id The role ID
     * @return The role
     * @throws EntityNotFoundException if role is not found
     */
    @Transactional(readOnly = true)
    public Role getRoleById(Long id) {
        return roleRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Роль не найдена с id " + id));
    }
    
    /**
     * Get a role by its name.
     * 
     * @param name The role name
     * @return The role
     * @throws EntityNotFoundException if role is not found
     */
    @Transactional(readOnly = true)
    public Role getRoleByName(String name) {
        return roleRepository.findByName(name)
                .orElseThrow(() -> new EntityNotFoundException("Роль не найдена с именем " + name));
    }
    
    /**
     * Find a role by its name.
     * 
     * @param name The role name
     * @return Optional containing the role if found
     */
    @Transactional(readOnly = true)
    public Optional<Role> findByName(String name) {
        return roleRepository.findByName(name);
    }
    
    /**
     * Create a new role.
     * 
     * @param role The role to create
     * @return The created role
     */
    @Transactional
    public Role createRole(Role role) {
        return roleRepository.save(role);
    }
    
    /**
     * Update an existing role.
     * 
     * @param id The role ID
     * @param roleDetails The updated role details
     * @return The updated role
     */
    @Transactional
    public Role updateRole(Long id, Role roleDetails) {
        Role role = getRoleById(id);
        role.setName(roleDetails.getName());
        role.setDescription(roleDetails.getDescription());
        return roleRepository.save(role);
    }
    
    /**
     * Delete a role.
     * 
     * @param id The role ID
     */
    @Transactional
    public void deleteRole(Long id) {
        roleRepository.deleteById(id);
    }
    
    /**
     * Add a permission to a role.
     * 
     * @param roleId The role ID
     * @param permissionId The permission ID
     * @return The updated role
     */
    @Transactional
    public Role addPermissionToRole(Long roleId, Long permissionId) {
        Role role = getRoleById(roleId);
        Permission permission = permissionRepository.findById(permissionId)
                .orElseThrow(() -> new EntityNotFoundException("Разрешение не найдено с id " + permissionId));
        
        role.getPermissions().add(permission);
        return roleRepository.save(role);
    }
    
    /**
     * Remove a permission from a role.
     * 
     * @param roleId The role ID
     * @param permissionId The permission ID
     * @return The updated role
     */
    @Transactional
    public Role removePermissionFromRole(Long roleId, Long permissionId) {
        Role role = getRoleById(roleId);
        Permission permission = permissionRepository.findById(permissionId)
                .orElseThrow(() -> new EntityNotFoundException("Разрешение не найдено с id " + permissionId));
        
        role.getPermissions().remove(permission);
        return roleRepository.save(role);
    }
    
    /**
     * Set permissions for a role.
     * 
     * @param roleId The role ID
     * @param permissionIds The set of permission IDs
     * @return The updated role
     */
    @Transactional
    public Role setPermissionsToRole(Long roleId, Set<Long> permissionIds) {
        Role role = getRoleById(roleId);
        Set<Permission> permissions = new HashSet<>();
        
        for (Long permissionId : permissionIds) {
            Permission permission = permissionRepository.findById(permissionId)
                    .orElseThrow(() -> new EntityNotFoundException("Разрешение не найдено с id " + permissionId));
            permissions.add(permission);
        }
        
        role.setPermissions(permissions);
        return roleRepository.save(role);
    }

    /**
     * Get all roles for table.
     * 
     * @return List of RoleTableDTO
     */
    @Transactional(readOnly = true)
    public List<RoleTableDTO> getAllRolesForTable() {
        return roleRepository.findAll().stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    /**
     * Get a role table DTO by ID.
     * 
     * @param id The role ID
     * @return The RoleTableDTO
     */
    @Transactional(readOnly = true)
    public RoleTableDTO getRoleTableDTOById(Long id) {
        Role role = getRoleById(id);
        return convertToDTO(role);
    }

    /**
     * Create a new role from DTO.
     * 
     * @param roleDTO The RoleTableDTO
     * @return The created RoleTableDTO
     */
    @Transactional
    public RoleTableDTO createRoleFromDTO(RoleTableDTO roleDTO) {
        Role role = new Role();
        role.setName(roleDTO.getName());
        role.setDescription(roleDTO.getDescription());
        
        if (roleDTO.getPermissionIds() != null) {
            Set<Permission> permissions = new HashSet<>();
            for (Long permissionId : roleDTO.getPermissionIds()) {
                Permission permission = permissionRepository.findById(permissionId)
                        .orElseThrow(() -> new EntityNotFoundException("Разрешение не найдено с id " + permissionId));
                permissions.add(permission);
            }
            role.setPermissions(permissions);
        }
        
        Role savedRole = roleRepository.save(role);
        return convertToDTO(savedRole);
    }

    /**
     * Update an existing role from DTO.
     * 
     * @param id The role ID
     * @param roleDTO The updated RoleTableDTO
     * @return The updated RoleTableDTO
     */
    @Transactional
    public RoleTableDTO updateRoleFromDTO(Long id, RoleTableDTO roleDTO) {
        Role role = getRoleById(id);
        role.setName(roleDTO.getName());
        role.setDescription(roleDTO.getDescription());
        
        if (roleDTO.getPermissionIds() != null) {
            Set<Permission> permissions = new HashSet<>();
            for (Long permissionId : roleDTO.getPermissionIds()) {
                Permission permission = permissionRepository.findById(permissionId)
                        .orElseThrow(() -> new EntityNotFoundException("Разрешение не найдено с id " + permissionId));
                permissions.add(permission);
            }
            role.setPermissions(permissions);
        }
        
        Role updatedRole = roleRepository.save(role);
        return convertToDTO(updatedRole);
    }

    // Helper method to convert Role to RoleTableDTO
    private RoleTableDTO convertToDTO(Role role) {
        RoleTableDTO dto = new RoleTableDTO();
        dto.setId(role.getId());
        dto.setName(role.getName());
        dto.setDescription(role.getDescription());
        dto.setPermissionIds(role.getPermissions().stream()
                .map(Permission::getId)
                .collect(Collectors.toSet()));
        return dto;
    }
} 