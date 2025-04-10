package com.citrusmall.citrusstock.service;

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
} 