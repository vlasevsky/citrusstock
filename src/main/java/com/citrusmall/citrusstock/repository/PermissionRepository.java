package com.citrusmall.citrusstock.repository;

import com.citrusmall.citrusstock.model.Permission;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

/**
 * Repository for accessing Permission entities.
 */
@Repository
public interface PermissionRepository extends JpaRepository<Permission, Long> {
    /**
     * Find a permission by its name.
     * 
     * @param name The permission name
     * @return Optional containing the permission if found
     */
    Optional<Permission> findByName(String name);
} 