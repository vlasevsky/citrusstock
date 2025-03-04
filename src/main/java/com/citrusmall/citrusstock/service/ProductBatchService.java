package com.citrusmall.citrusstock.service;

import com.citrusmall.citrusstock.dto.ProductBatchCreateRequest;
import com.citrusmall.citrusstock.dto.ProductBatchFilterCriteria;
import com.citrusmall.citrusstock.model.*;
import com.citrusmall.citrusstock.model.enums.GoodsStatus;
import com.citrusmall.citrusstock.repository.*;
import com.citrusmall.citrusstock.specification.ProductBatchSpecification;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Service
public class ProductBatchService {

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
        ProductBatchSpecification spec = new ProductBatchSpecification(criteria);
        return productBatchRepository.findAll(spec, pageable);
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
}
