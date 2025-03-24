package com.citrusmall.citrusstock.util.codegen;

import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.PDPageContentStream;
import org.apache.pdfbox.pdmodel.graphics.image.PDImageXObject;
import org.apache.pdfbox.pdmodel.font.PDType1Font;
import org.apache.pdfbox.pdmodel.common.PDRectangle;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;

public class QRCodeWithTextUtil {

    public static byte[] addTextBelowQRCode(byte[] qrImageBytes, String text) throws Exception {
        // Создаем документ PDF
        PDDocument document = new PDDocument();
        
        try {
            // Добавляем страницу A4
            PDPage page = new PDPage(PDRectangle.A4);
            document.addPage(page);
            
            // Создаем изображение из массива байтов
            PDImageXObject pdImage = PDImageXObject.createFromByteArray(document, qrImageBytes, null);
            
            // Получаем размеры страницы
            float pageWidth = page.getMediaBox().getWidth();
            float pageHeight = page.getMediaBox().getHeight();
            
            // Определяем размер QR-кода (200x200 пикселей)
            float qrSize = 200;
            
            // Позиционируем QR-код по центру страницы
            float x = (pageWidth - qrSize) / 2;
            float y = pageHeight - 250;
            
            // Создаем контент на странице
            PDPageContentStream contentStream = new PDPageContentStream(document, page);
            
            // Рисуем QR-код
            contentStream.drawImage(pdImage, x, y, qrSize, qrSize);
            
            // Добавляем текст
            contentStream.beginText();
            contentStream.setFont(PDType1Font.HELVETICA, 12);
            
            // Центрируем текст
            float textWidth = PDType1Font.HELVETICA.getStringWidth(text) / 1000 * 12;
            float textX = (pageWidth - textWidth) / 2;
            float textY = y - 20;
            
            contentStream.newLineAtOffset(textX, textY);
            contentStream.showText(text);
            contentStream.endText();
            
            // Закрываем контент-стрим
            contentStream.close();
            
            // Сохраняем PDF в массив байтов
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            document.save(baos);
            
            return baos.toByteArray();
        } finally {
            // Закрываем документ в блоке finally
            document.close();
        }
    }
}
