package com.citrusmall.citrusstock.util;


import com.citrusmall.citrusstock.model.Box;
import com.citrusmall.citrusstock.model.Product;
import com.citrusmall.citrusstock.model.ProductBatch;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.util.HashMap;
import java.util.Map;

public class QRCodeContentBuilder {

    private static final ObjectMapper mapper = new ObjectMapper();

    public static String buildContent(Box box) throws Exception {
        if (box == null) {
            throw new IllegalArgumentException("Box object cannot be null");
        }
        if (box.getId() == null) {
            throw new IllegalArgumentException("Box ID is not provided");
        }

        ProductBatch batch = box.getProductBatch();
        if (batch == null) {
            throw new IllegalArgumentException("ProductBatch is not provided for the box");
        }
        if (batch.getId() == null) {
            throw new IllegalArgumentException("Batch ID is not provided");
        }
        if (batch.getTotalBoxes() == null) {
            throw new IllegalArgumentException("Total number of boxes is not provided for the batch");
        }

        Product product = batch.getProduct();
        if (product == null) {
            throw new IllegalArgumentException("Product is not provided for the batch");
        }
        if (product.getId() == null) {
            throw new IllegalArgumentException("Product ID is not provided");
        }
        if (product.getName() == null || product.getName().trim().isEmpty()) {
            throw new IllegalArgumentException("Product name is not provided");
        }

        Map<String, Object> data = new HashMap<>();
        data.put("productId", product.getId());
        data.put("productName", product.getName());
        data.put("batchId", batch.getId());
        data.put("totalBoxes", batch.getTotalBoxes());
        data.put("boxId", box.getId());

        return mapper.writeValueAsString(data);
    }
}