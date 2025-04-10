package com.citrusmall.citrusstock.repository;

import com.citrusmall.citrusstock.model.Role;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

/**
 * Repository for accessing Role entities.
 */
@Repository
public interface RoleRepository extends JpaRepository<Role, Long> {
    /**
     * Find a role by its name.
     * 
     * @param name The role name
     * @return Optional containing the role if found
     */
    Optional<Role> findByName(String name);
} 