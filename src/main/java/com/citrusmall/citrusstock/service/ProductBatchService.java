package com.citrusmall.citrusstock.service;

import com.citrusmall.citrusstock.dto.ProductBatchCreateRequest;
import com.citrusmall.citrusstock.dto.ProductBatchFilterCriteria;
import com.citrusmall.citrusstock.model.*;
import com.citrusmall.citrusstock.model.enums.GoodsStatus;
import com.citrusmall.citrusstock.repository.*;
import com.citrusmall.citrusstock.specification.ProductBatchSpecification;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Service
public class ProductBatchService {

    private static final Logger logger = LoggerFactory.getLogger(ProductBatchService.class);

    @Autowired
    private ProductBatchRepository productBatchRepository;

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private SupplierRepository supplierRepository;

    @Autowired
    private ZoneRepository zoneRepository;

    @Autowired
    private BoxRepository boxRepository;

    public ProductBatch createProductBatch(ProductBatch productBatch, Long productId, Long supplierId, Integer totalBoxes) {
        if (productId != null) {
            Product product = productRepository.findById(productId)
                    .orElseThrow(() -> new IllegalArgumentException("Product not found with id " + productId));
            productBatch.setProduct(product);
        }
        if (supplierId != null) {
            Supplier supplier = supplierRepository.findById(supplierId)
                    .orElseThrow(() -> new IllegalArgumentException("Supplier not found with id " + supplierId));
            productBatch.setSupplier(supplier);
        }
        if (productBatch.getReceivedAt() == null) {
            productBatch.setReceivedAt(LocalDateTime.now());
        }
        // Если зона не задана, устанавливаем дефолтную зону "RECEIVING"
        if (productBatch.getZone() == null) {
            Zone defaultZone = zoneRepository.findByName("RECEIVING")
                    .orElseThrow(() -> new IllegalStateException("Default zone 'RECEIVING' not found"));
            productBatch.setZone(defaultZone);
        }
        productBatch.setStatus(GoodsStatus.GENERATED);
        // Сохраняем партию
        ProductBatch savedBatch = productBatchRepository.save(productBatch);
        // Если указано количество коробок, создаём их
        if (totalBoxes != null && totalBoxes > 0) {
            List<Box> boxes = new ArrayList<>();
            for (int i = 0; i < totalBoxes; i++) {
                Box box = new Box();
                box.setProductBatch(savedBatch);
                box.setStatus(GoodsStatus.GENERATED);
                boxes.add(box);
            }
            boxRepository.saveAll(boxes);
            savedBatch.setBoxes(boxes);
            savedBatch = productBatchRepository.save(savedBatch);
        }
        return savedBatch;

    }
    public ProductBatch getProductBatchById(Long id) {
        return productBatchRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("ProductBatch not found with id " + id));
    }


    public Page<ProductBatch> getFilteredProductBatches(ProductBatchFilterCriteria criteria, Pageable pageable) {
        try {
            logger.info("Getting filtered product batches with criteria: {} and pageable: {}", criteria, pageable);
            ProductBatchSpecification spec = new ProductBatchSpecification(criteria);
            Page<ProductBatch> result = productBatchRepository.findAll(spec, pageable);
            logger.info("Successfully retrieved {} product batches", result.getTotalElements());
            return result;
        } catch (Exception e) {
            logger.error("Error getting filtered product batches: {}", e.getMessage(), e);
            throw new RuntimeException("Failed to get filtered product batches: " + e.getMessage(), e);
        }
    }

    public ProductBatch updateProductBatch(Long id, ProductBatchCreateRequest request) {
        ProductBatch batch = productBatchRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("ProductBatch not found with id " + id));

        // Обновляем время получения, если передано
        if (request.getReceivedAt() != null) {
            batch.setReceivedAt(request.getReceivedAt());
        } else {
            batch.setReceivedAt(LocalDateTime.now());
        }

        // Обновляем продукт, если указан productId
        if (request.getProductId() != null) {
            Product product = productRepository.findById(request.getProductId())
                    .orElseThrow(() -> new IllegalArgumentException("Product not found with id " + request.getProductId()));
            batch.setProduct(product);
        }

        // Обновляем поставщика, если указан supplierId
        if (request.getSupplierId() != null) {
            Supplier supplier = supplierRepository.findById(request.getSupplierId())
                    .orElseThrow(() -> new IllegalArgumentException("Supplier not found with id " + request.getSupplierId()));
            batch.setSupplier(supplier);
        }
        return productBatchRepository.save(batch);
    }

    public void deleteProductBatch(Long id) {
        productBatchRepository.deleteById(id);
    }

    public void updateBatchStatus(Long batchId, GoodsStatus newStatus, Zone newZone) {
        ProductBatch batch = productBatchRepository.findById(batchId)
                .orElseThrow(() -> new IllegalArgumentException("ProductBatch not found with id " + batchId));
        batch.setStatus(newStatus);
        batch.setZone(newZone);
        productBatchRepository.save(batch);
    }
    
    /**
     * Обновляет статус партии на основе статусов всех её коробок.
     * Реализует бизнес-правила:
     * 1. Если все коробки имеют статус SCANNED, устанавливает партии статус SCANNED
     * 2. Если все коробки имеют статус SHIPPED, устанавливает партии статус SHIPPED
     * 3. В других случаях (коробки в разных статусах), статус партии не меняется
     *
     * @param batchId    ID партии для обновления
     * @param targetZone Целевая зона для партии при обновлении статуса
     * @throws IllegalArgumentException если партия не найдена
     */
    public void updateBatchStatusBasedOnBoxes(Long batchId, Zone targetZone) {
        // Находим партию и все связанные с ней коробки
        ProductBatch batch = productBatchRepository.findById(batchId)
                .orElseThrow(() -> new IllegalArgumentException("ProductBatch not found with id " + batchId));
        
        List<Box> boxes = boxRepository.findByProductBatch_Id(batchId);
        
        // Если коробок нет, статус партии не меняем
        if (boxes.isEmpty()) {
            return;
        }
        
        // Проверяем, все ли коробки имеют одинаковый статус
        boolean allBoxesScanned = boxes.stream().allMatch(box -> box.getStatus() == GoodsStatus.SCANNED);
        boolean allBoxesShipped = boxes.stream().allMatch(box -> box.getStatus() == GoodsStatus.SHIPPED);
        
        // Применяем бизнес-правила обновления статуса партии
        if (allBoxesScanned) {
            updateBatchStatusAndZone(batch, GoodsStatus.SCANNED, targetZone);
        } else if (allBoxesShipped) {
            updateBatchStatusAndZone(batch, GoodsStatus.SHIPPED, targetZone);
        }
        // Если у коробок разные статусы, оставляем текущий статус партии
    }
    
    /**
     * Обновляет статус и зону партии и сохраняет изменения.
     * 
     * @param batch     Партия для обновления
     * @param newStatus Новый статус партии
     * @param newZone   Новая зона партии
     */
    private void updateBatchStatusAndZone(ProductBatch batch, GoodsStatus newStatus, Zone newZone) {
        batch.setStatus(newStatus);
        batch.setZone(newZone);
        productBatchRepository.save(batch);
    }
    
    /**
     * Находит все партии, у которых коробки имеют разные статусы.
     * Позволяет выявить проблемные партии, требующие внимания.
     *
     * @return Список партий с коробками в разных статусах
     */
    public List<ProductBatch> findBatchesWithMixedBoxStatuses() {
        List<ProductBatch> result = new ArrayList<>();
        
        // Получаем все партии
        List<ProductBatch> allBatches = productBatchRepository.findAll();
        
        // Проверяем каждую партию
        for (ProductBatch batch : allBatches) {
            if (hasMixedBoxStatuses(batch.getId())) {
                result.add(batch);
            }
        }
        
        return result;
    }
    
    /**
     * Проверяет, имеет ли партия коробки в разных статусах.
     *
     * @param batchId ID партии для проверки
     * @return true, если коробки имеют разные статусы, иначе false
     */
    private boolean hasMixedBoxStatuses(Long batchId) {
        List<Box> boxes = boxRepository.findByProductBatch_Id(batchId);
        
        // Если коробок нет или одна, статусы не могут быть разными
        if (boxes.size() <= 1) {
            return false;
        }
        
        // Берем статус первой коробки как эталон
        GoodsStatus firstStatus = boxes.get(0).getStatus();
        
        // Проверяем, все ли коробки имеют такой же статус
        return boxes.stream().anyMatch(box -> box.getStatus() != firstStatus);
    }
    
    /**
     * Возвращает статистику распределения коробок партии по статусам.
     *
     * @param batchId ID партии
     * @return Карта, где ключ - статус, значение - количество коробок
     */
    public java.util.Map<GoodsStatus, Long> getBoxStatusStatisticsForBatch(Long batchId) {
        List<Box> boxes = boxRepository.findByProductBatch_Id(batchId);
        
        // Группируем коробки по статусу и подсчитываем количество в каждом статусе
        return boxes.stream()
                .collect(java.util.stream.Collectors.groupingBy(
                        Box::getStatus,
                        java.util.stream.Collectors.counting()
                ));
    }
}
