package com.citrusmall.citrusstock.controller;

import com.citrusmall.citrusstock.dto.BoxResponse;
import com.citrusmall.citrusstock.dto.ProductBatchCreateRequest;
import com.citrusmall.citrusstock.dto.ProductBatchFilterCriteria;
import com.citrusmall.citrusstock.dto.ProductBatchResponse;
import com.citrusmall.citrusstock.mapper.BoxMapper;
import com.citrusmall.citrusstock.mapper.ProductBatchMapper;
import com.citrusmall.citrusstock.model.ProductBatch;
import com.citrusmall.citrusstock.service.BoxService;
import com.citrusmall.citrusstock.service.ProductBatchService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
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
        // Преобразуем DTO в сущность
        ProductBatch productBatch = productBatchMapper.toProductBatch(request);
        // Создаем партию, передавая productId, supplierId и totalBoxes из запроса
        ProductBatch savedBatch = productBatchService.createProductBatch(productBatch, request.getProductId(), request.getSupplierId(), request.getTotalBoxes());
        // Преобразуем сохраненную сущность в DTO для ответа
        ProductBatchResponse response = productBatchMapper.toProductBatchResponse(savedBatch);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/{id}")
    public ResponseEntity<ProductBatchResponse> getProductBatch(@PathVariable Long id) {
        ProductBatch batch = productBatchService.getProductBatchById(id);
        return ResponseEntity.ok(productBatchMapper.toProductBatchResponse(batch));
    }

    /**
     * Единый endpoint для получения партий с опциональными фильтрами и пагинацией.
     * Если параметры фильтрации не переданы, возвращаются все записи.
     *
     * Пример запроса:
     * GET /api/warehouse/product-batches?productId=1&status=REGISTERED&page=0&size=10&sort=receivedAt,desc
     */
    @GetMapping
    public ResponseEntity<Page<ProductBatchResponse>> getProductBatches(
            @ModelAttribute ProductBatchFilterCriteria criteria,
            Pageable pageable) {
        Page<ProductBatch> batches = productBatchService.getFilteredProductBatches(criteria, pageable);
        Page<ProductBatchResponse> responses = batches.map(productBatchMapper::toProductBatchResponse);
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