package com.citrusmall.citrusstock.strategy;

import com.citrusmall.citrusstock.service.QRCodeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;

@Component("pdf")
public class PdfQrOutputStrategy implements QrOutputStrategy {

    @Autowired
    private QRCodeService qrCodeService;

    @Override
    public byte[] generateOutput(Long batchId) throws Exception {
        return qrCodeService.generatePdfForBatch(batchId);
    }

    @Override
    public String getContentType() {
        return MediaType.APPLICATION_PDF_VALUE;
    }

    @Override
    public String getContentDisposition(Long batchId) {
        return "attachment; filename=qr_codes_batch_" + batchId + ".pdf";
    }
}