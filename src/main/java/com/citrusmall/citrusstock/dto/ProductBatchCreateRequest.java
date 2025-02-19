package com.citrusmall.citrusstock.dto;

import jakarta.validation.constraints.Min;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class ProductBatchCreateRequest {
    private Long productId;
    private Long supplierId;

    @Min(value = 0, message = "Total boxes must be non-negative")
    private Integer totalBoxes;
    private LocalDateTime receivedAt;
}
