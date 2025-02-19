package com.citrusmall.citrusstock.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class SupplierCreateRequest {
    @NotBlank(message = "Supplier name must not be blank")
    private String name;
}