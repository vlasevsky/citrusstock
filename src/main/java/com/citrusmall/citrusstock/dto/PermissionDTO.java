package com.citrusmall.citrusstock.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class PermissionDTO {
    private Long id;
    
    @NotBlank(message = "Название разрешения не должно быть пустым")
    private String name;
    
    private String description;
} 