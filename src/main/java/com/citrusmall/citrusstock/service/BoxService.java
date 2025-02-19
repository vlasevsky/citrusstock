package com.citrusmall.citrusstock.service;

import com.citrusmall.citrusstock.dto.BoxCreateRequest;
import com.citrusmall.citrusstock.model.Box;
import com.citrusmall.citrusstock.model.ProductBatch;
import com.citrusmall.citrusstock.model.enums.BoxStatus;
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
        ProductBatch batch = productBatchRepository.findById(request.getProductBatchId())
                .orElseThrow(() -> new IllegalArgumentException("ProductBatch not found with id " + request.getProductBatchId()));

        Box box = new Box();
        box.setProductBatch(batch);
        box.setStatus(BoxStatus.GENERATED);

        return boxRepository.save(box);
    }

    public List<Box> getBoxesByBatchId(Long batchId) {
        return boxRepository.findByProductBatch_Id(batchId);
    }

}