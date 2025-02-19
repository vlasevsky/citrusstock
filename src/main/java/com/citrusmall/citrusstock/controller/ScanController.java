package com.citrusmall.citrusstock.controller;

import com.citrusmall.citrusstock.service.ScanService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/**
 * REST controller for handling scan operations.
 */
@RestController
@RequestMapping("/api/warehouse/scans")
public class ScanController {

    @Autowired
    private ScanService scanService;

    /**
     * Endpoint for scanning a new box upon arrival.
     * Example: POST /api/warehouse/scans/new/{boxId}?userId=123
     *
     * @param boxId  the ID of the box
     * @param userId the ID of the scanning operator
     * @return a success message
     */
    @PostMapping("/new/{boxId}")
    public ResponseEntity<String> scanNewBox(@PathVariable Long boxId, @RequestParam Long userId) {
        try {
            scanService.scanNewBox(boxId, userId);
            return ResponseEntity.ok("Box scanned (new product) successfully.");
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.badRequest().body("Error scanning new box: " + e.getMessage());
        }
    }

    /**
     * Endpoint for scanning a box for shipment.
     * Example: POST /api/warehouse/scans/shipment/{boxId}?userId=123
     *
     * @param boxId  the ID of the box
     * @param userId the ID of the scanning operator
     * @return a success message
     */
    @PostMapping("/shipment/{boxId}")
    public ResponseEntity<String> scanBoxForShipment(@PathVariable Long boxId, @RequestParam Long userId) {
        try {
            scanService.scanBoxForShipment(boxId, userId);
            return ResponseEntity.ok("Box scanned for shipment successfully.");
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.badRequest().body("Error scanning box for shipment: " + e.getMessage());
        }
    }
}