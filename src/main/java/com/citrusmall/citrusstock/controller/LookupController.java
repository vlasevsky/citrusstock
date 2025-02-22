package com.citrusmall.citrusstock.controller;


import com.citrusmall.citrusstock.model.enums.BoxStatus;
import com.citrusmall.citrusstock.model.enums.ProductBatchStatus;
import com.citrusmall.citrusstock.model.enums.ScanMode;
import com.citrusmall.citrusstock.model.enums.Zone;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/lookups")
public class LookupController {

    @GetMapping("/box-status")
    public ResponseEntity<List<String>> getBoxStatuses() {
        List<String> statuses = Arrays.stream(BoxStatus.values())
                .map(Enum::name)

                .collect(Collectors.toList());
        return ResponseEntity.ok(statuses);
    }

    @GetMapping("/product-batch-status")
    public ResponseEntity<List<String>> getProductBatchStatuses() {
        List<String> statuses = Arrays.stream(ProductBatchStatus.values())
                .map(Enum::name)
                .collect(Collectors.toList());
        return ResponseEntity.ok(statuses);
    }

    @GetMapping("/zone")
    public ResponseEntity<List<String>> getZones() {
        List<String> zones = Arrays.stream(Zone.values())
                .map(Enum::name)
                .collect(Collectors.toList());
        return ResponseEntity.ok(zones);
    }

    @GetMapping("/scan-mode")
    public ResponseEntity<List<String>> getScanModes() {
        List<String> modes = Arrays.stream(ScanMode.values())
                .map(Enum::name)
                .collect(Collectors.toList());
        return ResponseEntity.ok(modes);
    }
}
