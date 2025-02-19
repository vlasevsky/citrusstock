package com.citrusmall.citrusstock.controller;

import com.citrusmall.citrusstock.dto.BoxResponse;
import com.citrusmall.citrusstock.dto.ProductBatchCreateRequest;
import com.citrusmall.citrusstock.dto.ProductBatchResponse;
import com.citrusmall.citrusstock.mapper.BoxMapper;
import com.citrusmall.citrusstock.mapper.ProductBatchMapper;
import com.citrusmall.citrusstock.model.ProductBatch;
import com.citrusmall.citrusstock.service.BoxService;
import com.citrusmall.citrusstock.service.ProductBatchService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/warehouse/product-batches")
public class ProductBatchController {

    @Autowired
    private ProductBatchService productBatchService;

    @Autowired
    private BoxService boxService;

    @Autowired
    private ProductBatchMapper productBatchMapper;

    @Autowired
    private BoxMapper boxMapper;

    @PostMapping
    public ResponseEntity<ProductBatchResponse> createProductBatch(@Valid @RequestBody ProductBatchCreateRequest request) {
        // Создаем партию
        ProductBatch savedBatch = productBatchService.createProductBatch(request);
        // Преобразуем в DTO для ответа
        ProductBatchResponse response = productBatchMapper.toProductBatchResponse(savedBatch);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/{id}")
    public ResponseEntity<ProductBatchResponse> getProductBatch(@PathVariable Long id) {
        ProductBatch batch = productBatchService.getProductBatchById(id);
        return ResponseEntity.ok(productBatchMapper.toProductBatchResponse(batch));
    }

    @GetMapping
    public ResponseEntity<List<ProductBatchResponse>> getAllProductBatches() {
        List<ProductBatchResponse> responses = productBatchService.getAllProductBatches().stream()
                .map(productBatchMapper::toProductBatchResponse)
                .collect(Collectors.toList());
        return ResponseEntity.ok(responses);
    }

    @PutMapping("/{id}")
    public ResponseEntity<ProductBatchResponse> updateProductBatch(@PathVariable Long id, @Valid @RequestBody ProductBatchCreateRequest request) {
        // todo нужно другое dto, я думаю (на будущее)
        ProductBatch batch = productBatchMapper.toProductBatch(request);
        ProductBatch updatedBatch = productBatchService.updateProductBatch(id, batch);
        return ResponseEntity.ok(productBatchMapper.toProductBatchResponse(updatedBatch));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteProductBatch(@PathVariable Long id) {
        productBatchService.deleteProductBatch(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/{batchId}/boxes")
    public ResponseEntity<List<BoxResponse>> getBoxesByProductBatch(@PathVariable Long batchId) {
        List<BoxResponse> responses = boxService.getBoxesByProductBatchId(batchId).stream()
                .map(boxMapper::toBoxResponse)
                .collect(Collectors.toList());
        return ResponseEntity.ok(responses);
    }
}