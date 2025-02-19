package com.citrusmall.citrusstock.dto;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class BoxResponse {
    private Long id;
    private String code;
    private Long productBatchId;  // теперь хранится идентификатор партии как Long
    private String status;
    private LocalDateTime scannedAt;
    private UserResponse scannedBy;
}