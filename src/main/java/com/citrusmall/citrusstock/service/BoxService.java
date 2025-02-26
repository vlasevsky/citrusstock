package com.citrusmall.citrusstock.service;

import com.citrusmall.citrusstock.dto.BoxCreateRequest;
import com.citrusmall.citrusstock.model.Box;
import com.citrusmall.citrusstock.model.ProductBatch;
import com.citrusmall.citrusstock.model.enums.GoodsStatus;
import com.citrusmall.citrusstock.repository.BoxRepository;
import com.citrusmall.citrusstock.repository.ProductBatchRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class BoxService {

    @Autowired
    private BoxRepository boxRepository;

    @Autowired
    private ProductBatchRepository productBatchRepository;

    public Box createBox(BoxCreateRequest request) {
        Box box = new Box();
        ProductBatch batch = productBatchRepository.findById(request.getProductBatchId())
                .orElseThrow(() -> new IllegalArgumentException("ProductBatch not found with id " + request.getProductBatchId()));
        box.setProductBatch(batch);
        box.setStatus(GoodsStatus.GENERATED);
        return boxRepository.save(box);
    }

    public Box getBoxById(Long id) {
        return boxRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Box not found with id " + id));
    }

    public List<Box> getAllBoxes() {
        return boxRepository.findAll();
    }

    public Box updateBox(Long id, Box boxDetails) {
        Box box = getBoxById(id);
        box.setCode(boxDetails.getCode());
        box.setStatus(boxDetails.getStatus());
        box.setScannedAt(boxDetails.getScannedAt());
        box.setScannedBy(boxDetails.getScannedBy());
        return boxRepository.save(box);
    }

    public void deleteBox(Long id) {
        boxRepository.deleteById(id);
    }

    public List<Box> getBoxesByProductBatchId(Long batchId) {
        return boxRepository.findByProductBatch_Id(batchId);
    }
}