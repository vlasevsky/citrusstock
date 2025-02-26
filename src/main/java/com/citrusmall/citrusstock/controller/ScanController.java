package com.citrusmall.citrusstock.controller;

import com.citrusmall.citrusstock.model.enums.ScanMode;
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
     * Единый эндпоинт для сканирования коробки.
     * Принимает параметр scanMode (ON_WAREHOUSE для новых товаров, SHIPMENT для отгрузки).
     *
     * Пример запроса:
     * POST /api/warehouse/scans/{boxId}?userId=123&scanMode=ON_WAREHOUSE
     *
     * @param boxId    ID коробки
     * @param userId   ID оператора
     * @param scanMode Режим сканирования (ON_WAREHOUSE или SHIPMENT)
     * @return сообщение об успешном выполнении операции или ошибке
     */
    @PostMapping("/{boxId}")
    public ResponseEntity<String> scanBox(@PathVariable Long boxId,
                                          @RequestParam Long userId,
                                          @RequestParam ScanMode scanMode) {
        try {
            scanService.scanBoxByMode(boxId, userId, scanMode);
            return ResponseEntity.ok("Box scanned successfully.");
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.badRequest().body("Error scanning box: " + e.getMessage());
        }
    }
}