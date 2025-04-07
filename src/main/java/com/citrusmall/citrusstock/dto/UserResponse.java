package com.citrusmall.citrusstock.dto;

import com.citrusmall.citrusstock.model.enums.Role;
import lombok.Data;
import java.time.Instant;

@Data
public class UserResponse {
    private Long id;
    private String username;
    private String email;
    private Role role;
    private boolean enabled;
    private Instant lastActiveAt;
    private Instant createdAt;
    private Instant updatedAt;
}