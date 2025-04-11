package com.citrusmall.citrusstock.dto;

import lombok.Data;
import java.util.Set;

@Data
public class RoleTableDTO {
    private Long id;
    private String name;
    private String description;
    private Set<Long> permissionIds;
} 