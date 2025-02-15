package com.citrusmall.citrusstock.strategy;

import com.citrusmall.citrusstock.service.QRCodeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;

@Component("zip")
public class ZipQrOutputStrategy implements QrOutputStrategy {

    @Autowired
    private QRCodeService qrCodeService;

    @Override
    public byte[] generateOutput(Long batchId) throws Exception {
        return qrCodeService.generateAndStoreQRCodesZipForBatch(batchId);
    }

    @Override
    public String getContentType() {
        return MediaType.APPLICATION_OCTET_STREAM_VALUE;
    }

    @Override
    public String getContentDisposition(Long batchId) {
        return "attachment; filename=qr_codes_batch_" + batchId + ".zip";
    }
}
