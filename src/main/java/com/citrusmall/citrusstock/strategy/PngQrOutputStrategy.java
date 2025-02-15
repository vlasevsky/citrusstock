package com.citrusmall.citrusstock.strategy;

import com.citrusmall.citrusstock.service.QRCodeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;

@Component("png")
public class PngQrOutputStrategy implements QrOutputStrategy {

    @Autowired
    private QRCodeService qrCodeService;

    @Override
    public byte[] generateOutput(Long batchId) throws Exception {
        Long firstBoxId = qrCodeService.getFirstBoxIdByBatchId(batchId);
        return qrCodeService.generateAndStoreQRCodeWithTextForBox(firstBoxId);
    }

    @Override
    public String getContentType() {
        return MediaType.IMAGE_PNG_VALUE;
    }

    @Override
    public String getContentDisposition(Long batchId) {
        return "inline; filename=qr_code_batch_" + batchId + ".png";
    }
}