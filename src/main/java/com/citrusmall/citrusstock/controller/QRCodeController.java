package com.citrusmall.citrusstock.controller;


import com.citrusmall.citrusstock.service.QRCodeService;
import com.citrusmall.citrusstock.service.QrOutputService;
import com.citrusmall.citrusstock.strategy.BoxQrOutputAdapter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Base64;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/qr")
public class QRCodeController {

    @Autowired
    private QrOutputService qrOutputService;
    
    @Autowired
    private QRCodeService qrCodeService;
    
    @Autowired
    private BoxQrOutputAdapter boxQrOutputAdapter;

    @GetMapping("/generate/batch/{batchId}")
    public ResponseEntity<byte[]> generateOutput(
            @PathVariable Long batchId,
            @RequestParam(value = "format", defaultValue = "pdf") String format) {
        try {
            byte[] output = qrOutputService.generateOutput(batchId, format);
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.parseMediaType(qrOutputService.getContentType(format)));
            headers.add("Content-Disposition", qrOutputService.getContentDisposition(batchId, format));
            return ResponseEntity.ok().headers(headers).body(output);
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.badRequest().body(null);
        }
    }
    

    /**
     * Возвращает существующий QR-код для коробки, но не генерирует новый, если его нет.
     * 
     * @param boxId идентификатор коробки
     * @return Base64-строка с QR-кодом или сообщение об отсутствии
     */
    @GetMapping("/box/{boxId}")
    public ResponseEntity<?> getQRCodeForBox(@PathVariable Long boxId) {
        try {
            String base64QrCode = qrCodeService.getQRCodeForBox(boxId);
            
            if (base64QrCode == null || base64QrCode.isEmpty()) {
                Map<String, String> response = new HashMap<>();
                response.put("message", "QR code not found for box " + boxId);
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
            }
            
            Map<String, String> response = new HashMap<>();
            response.put("qrCode", base64QrCode);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }
    
    /**
     * Генерирует QR-код для коробки, используя паттерн Стратегия.
     * Так же, как generateOutput для ProductBatch.
     * 
     * @param boxId идентификатор коробки
     * @param format формат вывода (по умолчанию "pdf")
     * @return файл с QR-кодом в указанном формате
     */
    @GetMapping("/generate/box/{boxId}")
    public ResponseEntity<byte[]> generateBoxOutput(
            @PathVariable Long boxId,
            @RequestParam(value = "format", defaultValue = "pdf") String format) {
        try {
            byte[] output = boxQrOutputAdapter.generateOutputForBox(boxId, format);
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.parseMediaType(boxQrOutputAdapter.getContentType(format)));
            headers.add("Content-Disposition", boxQrOutputAdapter.getContentDispositionForBox(boxId, format));
            return ResponseEntity.ok().headers(headers).body(output);
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.badRequest().body(null);
        }
    }
}
