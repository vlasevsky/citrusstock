package com.citrusmall.citrusstock.dto;

import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

@Data
public class ProductBatchResponse {
    private Long id;
    private ProductResponse product;
    private SupplierResponse supplier;
    private LocalDateTime receivedAt;
    private String status;
    private ZoneResponse zone;
    private List<BoxResponse> boxes;
}
