package com.citrusmall.citrusstock.dto;

import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

@Data
public class ProductBatchFilterCriteria {
    private List<Long> productIds;
    private List<Long> supplierIds;
    // Передаём статусы в виде строк (например, "GENERATED", "STICKED", "SCANNED", "SHIPPED").
    private List<String> statusList;
    private List<Long> zoneIds;
    private LocalDateTime receivedFrom;
    private LocalDateTime receivedTo;
}