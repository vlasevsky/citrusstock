package com.citrusmall.citrusstock.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Data;


@Data
public class BoxCreateRequest {
    @NotNull(message = "Product Batch ID must be provided")
    private Long productBatchId;
}
