package com.citrusmall.citrusstock.dto;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class ProductBatchFilterCriteria {
    private Long productId;
    private Long supplierId;
    // Используем Enum в виде строки – Spring Boot конвертирует её автоматически,
    // либо можно сделать поле типа ProductBatchStatus, если клиент передаёт корректное значение.
    private String status;
    // Поле для зоны – передаём имя зоны
    private String zone;
    private LocalDateTime receivedFrom;
    private LocalDateTime receivedTo;
}