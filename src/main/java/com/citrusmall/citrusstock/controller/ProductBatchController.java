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
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/warehouse/product-batches")
public class ProductBatchController {
    private static final Logger logger = LoggerFactory.getLogger(ProductBatchController.class);

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
        try {
            logger.info("Creating new product batch with request: {}", request);
            ProductBatch productBatch = productBatchMapper.toProductBatch(request);
            ProductBatch savedBatch = productBatchService.createProductBatch(productBatch, request.getProductId(), request.getSupplierId(), request.getTotalBoxes());
            ProductBatchResponse response = productBatchMapper.toProductBatchResponse(savedBatch);
            logger.info("Successfully created product batch with ID: {}", savedBatch.getId());
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            logger.error("Error creating product batch: {}", e.getMessage(), e);
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Error creating product batch: " + e.getMessage());
        }
    }

    @GetMapping("/{id}")
    public ResponseEntity<ProductBatchResponse> getProductBatch(@PathVariable Long id) {
        try {
            logger.info("Fetching product batch with ID: {}", id);
            ProductBatch batch = productBatchService.getProductBatchById(id);
            return ResponseEntity.ok(productBatchMapper.toProductBatchResponse(batch));
        } catch (Exception e) {
            logger.error("Error fetching product batch with ID {}: {}", id, e.getMessage(), e);
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Error fetching product batch: " + e.getMessage());
        }
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
        try {
            logger.info("Getting product batches with criteria: {} and pageable: {}", criteria, pageable);
            Page<ProductBatch> batches = productBatchService.getFilteredProductBatches(criteria, pageable);
            Page<ProductBatchResponse> response = batches.map(productBatchMapper::toProductBatchResponse);
            logger.info("Successfully retrieved {} product batches", response.getTotalElements());
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            logger.error("Error getting product batches: {}", e.getMessage(), e);
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, 
                "Failed to get product batches: " + e.getMessage());
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<ProductBatchResponse> updateProductBatch(@PathVariable Long id, @Valid @RequestBody ProductBatchCreateRequest request) {
        try {
            logger.info("Updating product batch with ID: {} and request: {}", id, request);
            ProductBatch batch = productBatchMapper.toProductBatch(request);
            ProductBatch updatedBatch = productBatchService.updateProductBatch(id, request);
            return ResponseEntity.ok(productBatchMapper.toProductBatchResponse(updatedBatch));
        } catch (Exception e) {
            logger.error("Error updating product batch with ID {}: {}", id, e.getMessage(), e);
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Error updating product batch: " + e.getMessage());
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteProductBatch(@PathVariable Long id) {
        try {
            logger.info("Deleting product batch with ID: {}", id);
            productBatchService.deleteProductBatch(id);
            return ResponseEntity.noContent().build();
        } catch (Exception e) {
            logger.error("Error deleting product batch with ID {}: {}", id, e.getMessage(), e);
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Error deleting product batch: " + e.getMessage());
        }
    }

    @GetMapping("/{batchId}/boxes")
    public ResponseEntity<List<BoxResponse>> getBoxesByProductBatch(@PathVariable Long batchId) {
        try {
            logger.info("Fetching boxes for product batch with ID: {}", batchId);
            List<BoxResponse> responses = boxService.getBoxesByProductBatchId(batchId).stream()
                    .map(boxMapper::toBoxResponse)
                    .collect(Collectors.toList());
            return ResponseEntity.ok(responses);
        } catch (Exception e) {
            logger.error("Error fetching boxes for product batch with ID {}: {}", batchId, e.getMessage(), e);
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Error fetching boxes: " + e.getMessage());
        }
    }

    /**
     * Получает список партий, у которых коробки имеют разные статусы.
     * Помогает выявить проблемные партии, требующие внимания пользователя.
     *
     * @return Список партий с коробками в разных статусах
     */
    @GetMapping("/mixed-status")
    public ResponseEntity<List<ProductBatchResponse>> getBatchesWithMixedBoxStatuses() {
        List<ProductBatch> batches = productBatchService.findBatchesWithMixedBoxStatuses();
        List<ProductBatchResponse> responses = batches.stream()
                .map(productBatchMapper::toProductBatchResponse)
                .collect(Collectors.toList());
        return ResponseEntity.ok(responses);
    }
    
    /**
     * Получает статистику распределения коробок по статусам для указанной партии.
     * Показывает, сколько коробок находится в каждом статусе.
     *
     * @param batchId ID партии
     * @return Карта со статистикой: ключ - название статуса, значение - количество коробок
     */
    @GetMapping("/{batchId}/box-status-statistics")
    public ResponseEntity<java.util.Map<String, Long>> getBoxStatusStatistics(@PathVariable Long batchId) {
        // Получаем статистику в виде Map<GoodsStatus, Long>
        java.util.Map<com.citrusmall.citrusstock.model.enums.GoodsStatus, Long> statistics = 
                productBatchService.getBoxStatusStatisticsForBatch(batchId);
        
        // Преобразуем ключи из GoodsStatus в строки для удобства клиента
        java.util.Map<String, Long> response = statistics.entrySet().stream()
                .collect(java.util.stream.Collectors.toMap(
                        entry -> entry.getKey().name(),  // GoodsStatus -> String
                        java.util.Map.Entry::getValue    // количество коробок
                ));
        
        return ResponseEntity.ok(response);
    }
}