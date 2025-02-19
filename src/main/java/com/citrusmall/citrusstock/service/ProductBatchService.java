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
        // Default totalBoxes to 1 if not provided or invalid
        int totalBoxes = (request.getTotalBoxes() == null || request.getTotalBoxes() < 1) ? 1 : request.getTotalBoxes();

        ProductBatch batch = new ProductBatch();

        // Set product if productId is provided; otherwise, leave null.
        if (request.getProductId() != null) {
            batch.setProduct(productRepository.findById(request.getProductId())
                    .orElseThrow(() -> new IllegalArgumentException("Product not found with id " + request.getProductId())));
        } else {
            batch.setProduct(null);
        }

        // Set supplier if supplierId is provided; otherwise, leave null.
        if (request.getSupplierId() != null) {
            batch.setSupplier(supplierRepository.findById(request.getSupplierId())
                    .orElseThrow(() -> new IllegalArgumentException("Supplier not found with id " + request.getSupplierId())));
        } else {
            batch.setSupplier(null);
        }

        batch.setTotalBoxes(totalBoxes);
        batch.setReceivedAt(request.getReceivedAt() != null ? request.getReceivedAt() : LocalDateTime.now());

        // Save the ProductBatch first.
        ProductBatch savedBatch = productBatchRepository.save(batch);

        // Generate boxes for the batch.
        List<Box> boxes = new ArrayList<>();
        for (int i = 0; i < totalBoxes; i++) {
            Box box = new Box();
            box.setProductBatch(savedBatch);
            box.setStatus(BoxStatus.GENERATED); // default status for new boxes
            boxes.add(box);
        }
        boxRepository.saveAll(boxes);
        savedBatch.setBoxes(boxes);
        return savedBatch;
    }


    /**
     * Updates the status and zone of the ProductBatch.
     *
     * @param batchId   the ID of the product batch
     * @param newStatus the new status to set (e.g., CONFIRMED or SHIPPED)
     * @param newZone   the new zone to set (e.g., STORAGE or SHIPMENT)
     */
    public void updateBatchStatus(Long batchId, ProductBatchStatus newStatus, Zone newZone) {
        ProductBatch batch = productBatchRepository.findById(batchId)
                .orElseThrow(() -> new IllegalArgumentException("ProductBatch not found with id " + batchId));
        batch.setStatus(newStatus);
        batch.setZone(newZone);
        productBatchRepository.save(batch);
    }

    /**
     * Checks if all boxes in a batch have the expected BoxStatus.
     * If true, updates the ProductBatch with the new status and zone.
     *
     * @param batchId           the ID of the product batch
     * @param expectedBoxStatus the expected BoxStatus (e.g., SCANNED or SHIPPED)
     * @param newBatchStatus    the new ProductBatchStatus to set (e.g., CONFIRMED or SHIPPED)
     * @param newZone           the new Zone to set (e.g., STORAGE or SHIPMENT)
     */
    public void checkAndUpdateBatchStatus(Long batchId, com.citrusmall.citrusstock.model.enums.BoxStatus expectedBoxStatus,
                                          ProductBatchStatus newBatchStatus, Zone newZone) {
        List<Box> boxes = boxRepository.findByProductBatch_Id(batchId);
        boolean allMatch = boxes.stream().allMatch(b -> b.getStatus() == expectedBoxStatus);
        if (allMatch) {
            ProductBatch batch = productBatchRepository.findById(batchId)
                    .orElseThrow(() -> new IllegalArgumentException("ProductBatch not found with id " + batchId));
            batch.setStatus(newBatchStatus);
            batch.setZone(newZone);
            productBatchRepository.save(batch);
        }
    }
}
