package com.citrusmall.citrusstock.dto;

import lombok.Data;

import java.time.LocalDateTime;

/**
 * DTO для возврата данных о созданной партии товара.
 * Содержит основные поля партии: идентификатор, productId, supplierId, receivedAt, статус и зону.
 */
@Data
public class ProductBatchCreateResponse {
    private Long id;
    private Long productId;
    private Long supplierId;
    private LocalDateTime receivedAt;
    private String status;
    private String zone;
}
