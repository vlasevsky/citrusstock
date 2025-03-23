package com.citrusmall.citrusstock.util.codegen;

import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.PDPageContentStream;
import org.apache.pdfbox.pdmodel.graphics.image.PDImageXObject;
import org.apache.pdfbox.pdmodel.font.PDType1Font;
import org.apache.pdfbox.pdmodel.common.PDRectangle;

import java.io.ByteArrayOutputStream;

public class QRCodeWithTextUtil {

    public static byte[] addTextBelowQRCode(byte[] qrImageBytes, String text) throws Exception {
        PDDocument document = new PDDocument();
        PDPage page = new PDPage(new PDRectangle(300, 360)); // 300x300 for QR + 60 for text
        document.addPage(page);

        // Add QR code
        PDImageXObject pdImage = PDImageXObject.createFromByteArray(document, qrImageBytes, "qr_code");
        PDPageContentStream contentStream = new PDPageContentStream(document, page);
        
        // Draw QR code at the top
        contentStream.drawImage(pdImage, 50, 60, 200, 200);

        // Add text below QR code
        contentStream.beginText();
        contentStream.setFont(PDType1Font.HELVETICA_BOLD, 16);
        contentStream.newLineAtOffset(50, 40);
        
        // Center the text
        float textWidth = PDType1Font.HELVETICA_BOLD.getStringWidth(text) / 1000 * 16;
        float pageWidth = page.getMediaBox().getWidth();
        float xOffset = (pageWidth - textWidth) / 2;
        contentStream.newLineAtOffset(xOffset, 0);
        
        contentStream.showText(text);
        contentStream.endText();
        contentStream.close();

        // Save to byte array
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        document.save(baos);
        document.close();
        return baos.toByteArray();
    }
}
