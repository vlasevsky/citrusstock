package com.citrusmall.citrusstock.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class ProductCreateRequest {

    @NotBlank(message = "Product name must not be blank")
    private String name;
}
