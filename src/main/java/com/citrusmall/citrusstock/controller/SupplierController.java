package com.citrusmall.citrusstock.controller;

import com.citrusmall.citrusstock.dto.SupplierCreateRequest;
import com.citrusmall.citrusstock.dto.SupplierResponse;
import com.citrusmall.citrusstock.mapper.SupplierMapper;
import com.citrusmall.citrusstock.model.Supplier;
import com.citrusmall.citrusstock.service.SupplierService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/warehouse/suppliers")
public class SupplierController {

    @Autowired
    private SupplierService supplierService;

    @Autowired
    private SupplierMapper supplierMapper;

    @PostMapping
    public ResponseEntity<SupplierResponse> createSupplier(@Valid @RequestBody SupplierCreateRequest request) {
        Supplier supplier = supplierMapper.toSupplier(request);
        Supplier savedSupplier = supplierService.createSupplier(supplier);
        return ResponseEntity.ok(supplierMapper.toSupplierResponse(savedSupplier));
    }

    @GetMapping("/{id}")
    public ResponseEntity<SupplierResponse> getSupplier(@PathVariable Long id) {
        Supplier supplier = supplierService.getSupplierById(id);
        return ResponseEntity.ok(supplierMapper.toSupplierResponse(supplier));
    }

    @GetMapping
    public ResponseEntity<List<SupplierResponse>> getAllSuppliers() {
        List<SupplierResponse> responses = supplierService.getAllSuppliers().stream()
                .map(supplierMapper::toSupplierResponse)
                .collect(Collectors.toList());
        return ResponseEntity.ok(responses);
    }

    @PutMapping("/{id}")
    public ResponseEntity<SupplierResponse> updateSupplier(@PathVariable Long id, @Valid @RequestBody SupplierCreateRequest request) {
        Supplier supplier = supplierMapper.toSupplier(request);
        Supplier updatedSupplier = supplierService.updateSupplier(id, supplier);
        return ResponseEntity.ok(supplierMapper.toSupplierResponse(updatedSupplier));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteSupplier(@PathVariable Long id) {
        supplierService.deleteSupplier(id);
        return ResponseEntity.noContent().build();
    }
}