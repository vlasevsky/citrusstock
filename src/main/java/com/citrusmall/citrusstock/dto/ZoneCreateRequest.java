package com.citrusmall.citrusstock.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.Data;

@Data
public class ZoneCreateRequest {
    @NotBlank(message = "Zone name must not be blank")
    private String name;

    @NotBlank(message = "Zone color must not be blank")
    @Pattern(regexp = "^#([A-Fa-f0-9]{6})$", message = "Color must be in HEX format, e.g., #FFFFFF")
    private String color;
}
