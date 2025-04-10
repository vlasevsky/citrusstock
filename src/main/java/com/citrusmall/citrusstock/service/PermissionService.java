package com.citrusmall.citrusstock.service;

import com.citrusmall.citrusstock.model.Permission;
import com.citrusmall.citrusstock.repository.PermissionRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

/**
 * Service for managing permissions.
 */
@Service
@RequiredArgsConstructor
public class PermissionService {
    
    private final PermissionRepository permissionRepository;
    
    /**
     * Get all permissions.
     * 
     * @return List of all permissions
     */
    public List<Permission> getAllPermissions() {
        return permissionRepository.findAll();
    }
    
    /**
     * Get a permission by its ID.
     * 
     * @param id The permission ID
     * @return The permission
     * @throws EntityNotFoundException if permission is not found
     */
    public Permission getPermissionById(Long id) {
        return permissionRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Разрешение не найдено с id " + id));
    }
    
    /**
     * Get a permission by its name.
     * 
     * @param name The permission name
     * @return The permission
     * @throws EntityNotFoundException if permission is not found
     */
    public Permission getPermissionByName(String name) {
        return permissionRepository.findByName(name)
                .orElseThrow(() -> new EntityNotFoundException("Разрешение не найдено с именем " + name));
    }
    
    /**
     * Find a permission by its name.
     * 
     * @param name The permission name
     * @return Optional containing the permission if found
     */
    public Optional<Permission> findByName(String name) {
        return permissionRepository.findByName(name);
    }
    
    /**
     * Create a new permission.
     * 
     * @param permission The permission to create
     * @return The created permission
     */
    @Transactional
    public Permission createPermission(Permission permission) {
        return permissionRepository.save(permission);
    }
    
    /**
     * Update an existing permission.
     * 
     * @param id The permission ID
     * @param permissionDetails The updated permission details
     * @return The updated permission
     */
    @Transactional
    public Permission updatePermission(Long id, Permission permissionDetails) {
        Permission permission = getPermissionById(id);
        permission.setName(permissionDetails.getName());
        permission.setDescription(permissionDetails.getDescription());
        return permissionRepository.save(permission);
    }
    
    /**
     * Delete a permission.
     * 
     * @param id The permission ID
     */
    @Transactional
    public void deletePermission(Long id) {
        permissionRepository.deleteById(id);
    }
} 