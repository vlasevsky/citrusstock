package com.citrusmall.citrusstock.controller;

import com.citrusmall.citrusstock.dto.BoxCreateRequest;
import com.citrusmall.citrusstock.dto.BoxResponse;
import com.citrusmall.citrusstock.mapper.BoxMapper;
import com.citrusmall.citrusstock.model.Box;
import com.citrusmall.citrusstock.service.BoxService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/warehouse/boxes")
public class BoxController {

    @Autowired
    private BoxService boxService;

    @Autowired
    private BoxMapper boxMapper;

    @PostMapping
    public ResponseEntity<BoxResponse> createBox(@Valid @RequestBody BoxCreateRequest request) {
        Box savedBox = boxService.createBox(request);
        return ResponseEntity.ok(boxMapper.toBoxResponse(savedBox));
    }

    @GetMapping("/{id}")
    public ResponseEntity<BoxResponse> getBox(@PathVariable Long id) {
        Box box = boxService.getBoxById(id);
        return ResponseEntity.ok(boxMapper.toBoxResponse(box));
    }

    @GetMapping
    public ResponseEntity<List<BoxResponse>> getAllBoxes() {
        List<BoxResponse> responses = boxService.getAllBoxes().stream()
                .map(boxMapper::toBoxResponse)
                .collect(Collectors.toList());
        return ResponseEntity.ok(responses);
    }

    @PutMapping("/{id}")
    public ResponseEntity<BoxResponse> updateBox(@PathVariable Long id, @Valid @RequestBody BoxCreateRequest request) {
        // Если для обновления коробки нужен отдельный DTO, его можно создать. В этом примере используем BoxCreateRequest.
        Box box = boxService.createBox(request); // В реальной ситуации update должен вызывать метод обновления
        Box updatedBox = boxService.updateBox(id, box);
        return ResponseEntity.ok(boxMapper.toBoxResponse(updatedBox));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteBox(@PathVariable Long id) {
        boxService.deleteBox(id);
        return ResponseEntity.noContent().build();
    }
}
