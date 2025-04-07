package com.citrusmall.citrusstock.dto;

import com.citrusmall.citrusstock.model.enums.Role;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class UserCreateRequest {
    @NotBlank(message = "Username must not be blank")
    private String username;
    @NotBlank(message = "Password must not be blank")
    private String password;
    @NotBlank(message = "Email must not be blank")
    @Email(message = "Email should be valid")
    private String email;
    @NotNull(message = "Role must not be null")
    private Role role;
}