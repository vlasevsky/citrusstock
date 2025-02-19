package com.citrusmall.citrusstock.controller;

import com.citrusmall.citrusstock.dto.ProductCreateRequest;
import com.citrusmall.citrusstock.model.Product;
import com.citrusmall.citrusstock.repository.ProductRepository;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/warehouse/products")
public class ProductController {

    @Autowired
    private ProductRepository productRepository;

    @PostMapping
    public ResponseEntity<Product> createProduct(@Valid @RequestBody ProductCreateRequest request) {
        Product product = new Product();
        product.setName(request.getName());
        Product savedProduct = productRepository.save(product);
        return ResponseEntity.ok(savedProduct);
    }
}