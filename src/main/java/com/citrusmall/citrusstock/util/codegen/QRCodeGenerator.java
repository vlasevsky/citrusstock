package com.citrusmall.citrusstock.util.codegen;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.WriterException;
import com.google.zxing.client.j2se.MatrixToImageConfig;
import com.google.zxing.client.j2se.MatrixToImageWriter;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.QRCodeWriter;
import org.springframework.stereotype.Service;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

@Service
public class QRCodeGenerator implements CodeGenerator {

    @Override
    public byte[] generateCodeImage(String content, int width, int height) throws Exception {
        QRCodeWriter qrCodeWriter = new QRCodeWriter();
        BitMatrix bitMatrix;
        try {
            bitMatrix = qrCodeWriter.encode(content, BarcodeFormat.QR_CODE, width, height);
        } catch (WriterException e) {
            throw new Exception("Error generating QR code", e);
        }

        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        try {
            MatrixToImageWriter.writeToStream(bitMatrix, "PNG", outputStream, new MatrixToImageConfig());
        } catch (IOException e) {
            throw new Exception("Error writing QR code to stream", e);
        }
        return outputStream.toByteArray();
    }
}
