package com.citrusmall.citrusstock.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class ZoneStats {
    private Long id;
    private String name;
    private String color;
    private Long productBatchCount;
}
