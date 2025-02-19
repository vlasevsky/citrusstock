package com.citrusmall.citrusstock.controller;

import com.citrusmall.citrusstock.dto.ProductBatchCreateRequest;
import com.citrusmall.citrusstock.model.ProductBatch;
import com.citrusmall.citrusstock.service.ProductBatchService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/warehouse/product-batches")
public class ProductBatchController {

    @Autowired
    private ProductBatchService productBatchService;

    @PostMapping
    public ResponseEntity<ProductBatch> createProductBatch(@Valid @RequestBody ProductBatchCreateRequest request) {
        ProductBatch savedBatch = productBatchService.createProductBatch(request);
        return ResponseEntity.ok(savedBatch);
    }
}