package com.citrusmall.citrusstock.controller;

import com.citrusmall.citrusstock.dto.BoxCreateRequest;
import com.citrusmall.citrusstock.model.Box;
import com.citrusmall.citrusstock.service.BoxService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/warehouse/boxes")
public class BoxController {

    @Autowired
    private BoxService boxService;

    @PostMapping
    public ResponseEntity<Box> createBox(@Valid @RequestBody BoxCreateRequest request) {
        Box savedBox = boxService.createBox(request);
        return ResponseEntity.ok(savedBox);
    }
}
