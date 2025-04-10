package com.citrusmall.citrusstock.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Data
public class RoleDTO {
    private Long id;
    
    @NotBlank(message = "Название роли не должно быть пустым")
    private String name;
    
    private String description;
    
    private Set<Long> permissionIds = new HashSet<>();
    
    private List<PermissionDTO> permissions = new ArrayList<>();
} 