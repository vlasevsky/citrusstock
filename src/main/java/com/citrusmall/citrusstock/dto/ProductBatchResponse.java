package com.citrusmall.citrusstock.dto;

import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

@Data
public class ProductBatchResponse {
    private Long id;
    private Long productId;
    private Long supplierId;
    private LocalDateTime receivedAt;
    private String status;
    private Long zone;
    private List<BoxResponse> boxes;
}
