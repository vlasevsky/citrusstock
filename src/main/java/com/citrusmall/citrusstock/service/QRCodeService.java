package com.citrusmall.citrusstock.service;

import com.citrusmall.citrusstock.model.Box;
import com.citrusmall.citrusstock.model.Product;
import com.citrusmall.citrusstock.model.ProductBatch;
import com.citrusmall.citrusstock.repository.BoxRepository;
import com.citrusmall.citrusstock.util.FileStorageUtil;
import com.citrusmall.citrusstock.util.QRCodeContentBuilder;
import com.citrusmall.citrusstock.util.codegen.CodeGenerator;
import com.citrusmall.citrusstock.util.codegen.CodeGeneratorFactory;
import com.citrusmall.citrusstock.util.codegen.QRCodeWithTextUtil;
import org.apache.pdfbox.multipdf.PDFMergerUtility;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Base64;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

@Service
public class QRCodeService {

    private static final int QR_CODE_SIZE = 200;
    private static final String QR_CODES_DIR = "qr/codes";

    @Autowired
    private BoxRepository boxRepository;

    @Autowired
    private CodeGeneratorFactory codeGeneratorFactory;

    /**
     * Генерирует QR-код для коробки и обновляет её поле code.
     *
     * @param boxId идентификатор коробки
     * @return строка в формате Base64, содержащая QR-код
     * @throws Exception в случае ошибки генерации
     */
    public String generateQRCodeForBox(Long boxId) throws Exception {
        Box box = getBoxById(boxId);
        byte[] qrImageBytes = generateQRCodeBytesForBox(boxId);
        String base64Image = Base64.getEncoder().encodeToString(qrImageBytes);

        box.setCode(base64Image);
        boxRepository.save(box);

        return base64Image;
    }

    /**
     * Возвращает существующий QR-код для коробки или null, если его нет.
     * Не генерирует QR-код, только возвращает существующий.
     *
     * @param boxId идентификатор коробки
     * @return строка в формате Base64, содержащая QR-код, или null
     * @throws Exception если коробка не найдена
     */
    public String getQRCodeForBox(Long boxId) throws Exception {
        Box box = getBoxById(boxId);
        return box.getCode();
    }

    /**
     * Генерирует QR-код для коробки с текстом продукта под ним,
     * сохраняет его на диск и возвращает в виде массива байтов PDF.
     *
     * @param boxId идентификатор коробки
     * @return массив байтов, содержащий PDF с QR-кодом и текстом
     * @throws Exception в случае ошибки генерации
     */
    public byte[] generateAndStoreQRCodeWithTextForBox(Long boxId) throws Exception {
        // Находим коробку по ID и обеспечиваем наличие QR-кода
        Box box = getBoxAndEnsureQRCode(boxId);

        // Генерируем бинарный QR-код
        byte[] qrImageBytes = generateQRCodeBytesForBox(boxId);

        // Получаем название продукта для текста под QR-кодом
        String productName = box.getProductBatch().getProduct().getName();

        // Создаем PDF с QR-кодом и текстом продукта
        byte[] pdfBytes = QRCodeWithTextUtil.addTextBelowQRCode(qrImageBytes, productName);

        // Сохраняем PDF-файл в директорию
        savePdfFile(box, pdfBytes);

        // Возвращаем PDF как массив байтов
        return pdfBytes;
    }

    /**
     * Generates a PDF file where each page contains the QR code (with text)
     * for each box in the given product batch.
     *
     * @param batchId the identifier of the ProductBatch
     * @return a byte array containing the PDF file
     * @throws Exception if an error occurs during generation
     */
    public byte[] generatePdfForBatch(Long batchId) throws Exception {
        List<Box> boxes = getBoxesForBatch(batchId);

        PDFMergerUtility merger = new PDFMergerUtility();
        ByteArrayOutputStream baos = new ByteArrayOutputStream();

        try {
            // Добавляем все PDF с QR-кодами в merger
            for (Box box : boxes) {
                byte[] pdfBytes = generateAndStoreQRCodeWithTextForBox(box.getId());
                merger.addSource(new ByteArrayInputStream(pdfBytes));
            }

            // Объединяем все PDF-документы
            merger.setDestinationStream(baos);
            merger.mergeDocuments(null);

            return baos.toByteArray();
        } finally {
            baos.close();
        }
    }

    /**
     * Generates a ZIP archive containing PDF files of QR codes for all boxes
     * in the given product batch.
     *
     * @param batchId the identifier of the ProductBatch
     * @return a byte array containing the ZIP archive
     * @throws Exception if an error occurs during generation
     */
    public byte[] generateAndStoreQRCodesZipForBatch(Long batchId) throws Exception {
        List<Box> boxes = getBoxesForBatch(batchId);

        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        try (ZipOutputStream zos = new ZipOutputStream(baos)) {
            for (Box box : boxes) {
                byte[] pdfBytes = generateAndStoreQRCodeWithTextForBox(box.getId());
                String fileName = "qr_box_" + box.getId() + ".pdf";
                zos.putNextEntry(new ZipEntry(fileName));
                zos.write(pdfBytes);
                zos.closeEntry();
            }
        }
        return baos.toByteArray();
    }

    /**
     * Helper method to get the ID of the first box in the given product batch.
     * Used for returning a single PDF output.
     *
     * @param batchId the identifier of the ProductBatch
     * @return the ID of the first box
     * @throws Exception if no boxes are found
     */
    public Long getFirstBoxIdByBatchId(Long batchId) throws Exception {
        List<Box> boxes = getBoxesForBatch(batchId);
        return boxes.get(0).getId();
    }

    // Private helper methods

    /**
     * Получает коробку по идентификатору.
     *
     * @param boxId идентификатор коробки
     * @return найденная коробка
     * @throws IllegalArgumentException если коробка не найдена
     */
    private Box getBoxById(Long boxId) {
        return boxRepository.findById(boxId)
                .orElseThrow(() -> new IllegalArgumentException("Box with id " + boxId + " not found"));
    }

    /**
     * Получает список коробок для партии.
     *
     * @param batchId идентификатор партии
     * @return список коробок
     * @throws IllegalArgumentException если коробки не найдены
     */
    private List<Box> getBoxesForBatch(Long batchId) {
        List<Box> boxes = boxRepository.findByProductBatch_Id(batchId);
        if (boxes.isEmpty()) {
            throw new IllegalArgumentException("No boxes found for product batch id " + batchId);
        }
        return boxes;
    }

    /**
     * Получает коробку и гарантирует наличие QR-кода.
     *
     * @param boxId идентификатор коробки
     * @return коробка с QR-кодом
     * @throws Exception в случае ошибки
     */
    private Box getBoxAndEnsureQRCode(Long boxId) throws Exception {
        Box box = getBoxById(boxId);

        // Если у коробки нет QR-кода, генерируем его
        if (box.getCode() == null || box.getCode().isEmpty()) {
            generateQRCodeForBox(boxId);
            // Перезагружаем коробку с обновленным QR-кодом
            box = getBoxById(boxId);
        }

        return box;
    }

    /**
     * Генерирует бинарные данные QR-кода для указанной коробки.
     *
     * @param boxId идентификатор коробки
     * @return массив байтов с QR-кодом в формате PNG
     * @throws Exception в случае ошибки
     */
    public byte[] generateQRCodeBytesForBox(Long boxId) throws Exception {
        Box box = getBoxById(boxId);
        String qrContent = QRCodeContentBuilder.buildContent(box);
        CodeGenerator generator = codeGeneratorFactory.getGenerator("QR");
        return generator.generateCodeImage(qrContent, QR_CODE_SIZE, QR_CODE_SIZE);
    }

    /**
     * Сохраняет PDF-файл в соответствующую директорию.
     *
     * @param box      коробка
     * @param pdfBytes содержимое PDF-файла
     * @throws IOException в случае ошибки ввода-вывода
     */
    private void savePdfFile(Box box, byte[] pdfBytes) throws IOException {
        Long productId = box.getProductBatch().getProduct().getId();
        String filename = FileStorageUtil.generateQrCodePdfFileName(box.getId());
        FileStorageUtil.saveQrCodeFile(productId, filename, pdfBytes);
    }
}