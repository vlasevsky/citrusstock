package com.citrusmall.citrusstock.dto;

import com.citrusmall.citrusstock.model.enums.ScanMode;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ScanModeDTO {
    private ScanMode value;
    private String label;
}