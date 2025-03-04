package com.citrusmall.citrusstock.controller;


import com.citrusmall.citrusstock.model.enums.*;
import com.citrusmall.citrusstock.util.EnumLocalizer;
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

    @GetMapping("/good-status")
    public ResponseEntity<List<String>> getBoxStatuses() {
        List<String> statuses = Arrays.stream(GoodsStatus.values())
                .map(EnumLocalizer::localizeGoodsStatus)
                .collect(Collectors.toList());
        return ResponseEntity.ok(statuses);
    }



    @GetMapping("/scan-mode")
    public ResponseEntity<List<String>> getScanModes() {
        List<String> modes = Arrays.stream(ScanMode.values())
                .map(EnumLocalizer::localizeScanMode)
                .collect(Collectors.toList());
        return ResponseEntity.ok(modes);
    }
}
