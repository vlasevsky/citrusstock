package com.citrusmall.citrusstock.dto;

import jakarta.validation.constraints.NotEmpty;
import lombok.Data;

import java.util.List;

@Data
public class RolePermissionsDTO {
    @NotEmpty(message = "Список ID разрешений не должен быть пустым")
    private List<Long> permissionIds;
} 