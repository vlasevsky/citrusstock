package com.citrusmall.citrusstock.service;

import com.citrusmall.citrusstock.dto.ProductBatchCreateRequest;
import com.citrusmall.citrusstock.model.Box;
import com.citrusmall.citrusstock.model.ProductBatch;
import com.citrusmall.citrusstock.model.enums.BoxStatus;
import com.citrusmall.citrusstock.model.enums.ProductBatchStatus;
import com.citrusmall.citrusstock.model.enums.Zone;
import com.citrusmall.citrusstock.repository.BoxRepository;
import com.citrusmall.citrusstock.repository.ProductBatchRepository;
import com.citrusmall.citrusstock.repository.ProductRepository;
import com.citrusmall.citrusstock.repository.SupplierRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Service
public class ProductBatchService {

    @Autowired
    private ProductBatchRepository productBatchRepository;

    @Autowired
    private BoxRepository boxRepository;

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private SupplierRepository supplierRepository;

    public ProductBatch createProductBatch(ProductBatchCreateRequest request) {
        ProductBatch batch = new ProductBatch();

        if (request.getProductId() != null) {
            batch.setProduct(productRepository.findById(request.getProductId())
                    .orElseThrow(() -> new IllegalArgumentException("Product not found with id " + request.getProductId())));
        } else {
            batch.setProduct(null);
        }

        if (request.getSupplierId() != null) {
            batch.setSupplier(supplierRepository.findById(request.getSupplierId())
                    .orElseThrow(() -> new IllegalArgumentException("Supplier not found with id " + request.getSupplierId())));
        } else {
            batch.setSupplier(null);
        }

        batch.setReceivedAt(request.getReceivedAt() != null ? request.getReceivedAt() : LocalDateTime.now());
        batch.setStatus(ProductBatchStatus.REGISTERED);
        batch.setZone(Zone.RECEIVING);

        // Сохраняем партию
        ProductBatch savedBatch = productBatchRepository.save(batch);

        // Генерируем коробки в количестве, указанном в DTO (если не указано, по умолчанию 1)
        int totalBoxes = (request.getTotalBoxes() == null || request.getTotalBoxes() < 1) ? 1 : request.getTotalBoxes();
        List<Box> boxes = new ArrayList<>();
        for (int i = 0; i < totalBoxes; i++) {
            Box box = new Box();
            box.setProductBatch(savedBatch);
            box.setStatus(BoxStatus.GENERATED);
            boxes.add(box);
        }
        boxRepository.saveAll(boxes);
        savedBatch.setBoxes(boxes);
        return savedBatch;
    }

    public void updateBatchStatus(Long batchId, ProductBatchStatus newStatus, Zone newZone) {
        ProductBatch batch = productBatchRepository.findById(batchId)
                .orElseThrow(() -> new IllegalArgumentException("ProductBatch not found with id " + batchId));
        batch.setStatus(newStatus);
        batch.setZone(newZone);
        productBatchRepository.save(batch);
    }

    public ProductBatch getProductBatchById(Long id) {
        return productBatchRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("ProductBatch not found with id " + id));
    }

    public List<ProductBatch> getAllProductBatches() {
        return productBatchRepository.findAll();
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


}
