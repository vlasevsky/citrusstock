package com.citrusmall.citrusstock.service;

import com.citrusmall.citrusstock.dto.PermissionTableDTO;
import com.citrusmall.citrusstock.model.Permission;
import com.citrusmall.citrusstock.repository.PermissionRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class PermissionService {
    
    private final PermissionRepository permissionRepository;

    @Transactional(readOnly = true)
    public Permission getPermissionById(Long id) {
        return permissionRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Разрешение не найдено с id " + id));
    }

    @Transactional(readOnly = true)
    public List<PermissionTableDTO> getAllPermissionsForTable() {
        return permissionRepository.findAll().stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    @Transactional
    public PermissionTableDTO updatePermission(Long id, PermissionTableDTO permissionDTO) {
        Permission permission = getPermissionById(id);
        
        if (permissionDTO.getDescription() != null) {
            permission.setDescription(permissionDTO.getDescription());
        }
        
        if (permissionDTO.getCategory() != null) {
            permission.setCategory(permissionDTO.getCategory());
        }
        
        Permission updatedPermission = permissionRepository.save(permission);
        return convertToDTO(updatedPermission);
    }

    private PermissionTableDTO convertToDTO(Permission permission) {
        PermissionTableDTO dto = new PermissionTableDTO();
        dto.setId(permission.getId());
        dto.setName(permission.getName());
        dto.setDescription(permission.getDescription());
        dto.setCategory(permission.getCategory());
        return dto;
    }
} 