package com.citrusmall.citrusstock.controller;


import com.citrusmall.citrusstock.service.QRCodeService;
import com.citrusmall.citrusstock.service.QrOutputService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/qr")
public class QRCodeController {

    @Autowired
    private QrOutputService qrOutputService;

    /**
     * Unified endpoint for generating QR code outputs for a product batch.
     * The "format" parameter specifies the desired output format ("pdf", "zip", or "png").
     *
     * Example: /api/qr/generate/batch/1?format=pdf
     *
     * @param batchId the identifier of the ProductBatch
     * @param format  the desired format (default is "pdf")
     * @return HTTP response containing the generated output
     */
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
}
