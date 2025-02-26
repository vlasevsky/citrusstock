package com.citrusmall.citrusstock.service;

import com.citrusmall.citrusstock.dto.ProductBatchFilterCriteria;
import com.citrusmall.citrusstock.model.ProductBatch;
import com.citrusmall.citrusstock.model.Zone;
import com.citrusmall.citrusstock.model.enums.GoodsStatus;
import com.citrusmall.citrusstock.repository.ProductBatchRepository;
import com.citrusmall.citrusstock.repository.ProductRepository;
import com.citrusmall.citrusstock.repository.SupplierRepository;
import com.citrusmall.citrusstock.repository.ZoneRepository;
import com.citrusmall.citrusstock.specification.ProductBatchSpecification;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

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

    public ProductBatch createProductBatch(ProductBatch productBatch) {
        if (productBatch.getReceivedAt() == null) {
            productBatch.setReceivedAt(LocalDateTime.now());
        }
        // Если зона не задана, устанавливаем дефолтную "RECEIVING"
        if (productBatch.getZone() == null) {
            Zone defaultZone = zoneRepository.findByName("RECEIVING")
                    .orElseThrow(() -> new IllegalStateException("Default zone 'RECEIVING' not found"));
            productBatch.setZone(defaultZone);
        }
        productBatch.setStatus(GoodsStatus.GENERATED);
        return productBatchRepository.save(productBatch);
    }

    public ProductBatch getProductBatchById(Long id) {
        return productBatchRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("ProductBatch not found with id " + id));
    }


    public Page<ProductBatch> getFilteredProductBatches(ProductBatchFilterCriteria criteria, Pageable pageable) {
        ProductBatchSpecification spec = new ProductBatchSpecification(criteria);
        return productBatchRepository.findAll(spec, pageable);
    }

    public ProductBatch updateProductBatch(Long id, ProductBatch productBatchDetails) {
        ProductBatch batch = getProductBatchById(id);
        batch.setReceivedAt(productBatchDetails.getReceivedAt());
        batch.setStatus(productBatchDetails.getStatus());
        batch.setZone(productBatchDetails.getZone());
        batch.setProduct(productBatchDetails.getProduct());
        batch.setSupplier(productBatchDetails.getSupplier());
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
