package com.citrusmall.citrusstock.service;

import com.citrusmall.citrusstock.model.Box;
import com.citrusmall.citrusstock.model.Product;
import com.citrusmall.citrusstock.model.ProductBatch;
import com.citrusmall.citrusstock.repository.BoxRepository;
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
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

@Service
public class QRCodeService {

    @Autowired
    private BoxRepository boxRepository;

    @Autowired
    private CodeGeneratorFactory codeGeneratorFactory;

    /**
     * Generates a QR code with product name text for a specific box,
     * saves it to disk, and returns the image as a byte array (PDF).
     *
     * @param boxId the identifier of the box
     * @return a byte array containing the PDF file
     * @throws Exception if the box is not found or an error occurs during generation/storage
     */
    public byte[] generateAndStoreQRCodeWithTextForBox(Long boxId) throws Exception {
        Box box = boxRepository.findById(boxId)
                .orElseThrow(() -> new IllegalArgumentException("Box with id " + boxId + " not found"));

        String qrContent = QRCodeContentBuilder.buildContent(box);
        CodeGenerator generator = codeGeneratorFactory.getGenerator("QR");
        byte[] qrImageBytes = generator.generateCodeImage(qrContent, 200, 200);
        String productName = box.getProductBatch().getProduct().getName();
        byte[] pdfBytes = QRCodeWithTextUtil.addTextBelowQRCode(qrImageBytes, productName);

        // Save the file under "qr/codes/product_{productId}/qr_box_{boxId}.pdf"
        ProductBatch batch = box.getProductBatch();
        Product product = batch.getProduct();
        String productFolderName = "product_" + product.getId();
        Path storageBasePath = Paths.get("qr", "codes");
        Path productFolder = storageBasePath.resolve(productFolderName);
        if (!Files.exists(productFolder)) {
            Files.createDirectories(productFolder);
        }
        String filename = "qr_box_" + box.getId() + ".pdf";
        Path filePath = productFolder.resolve(filename);
        Files.write(filePath, pdfBytes);

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
        List<Box> boxes = boxRepository.findByProductBatch_Id(batchId);
        if (boxes.isEmpty()) {
            throw new IllegalArgumentException("No boxes found for product batch id " + batchId);
        }

        PDFMergerUtility merger = new PDFMergerUtility();
        ByteArrayOutputStream baos = new ByteArrayOutputStream();

        try {
            // Создаем временный документ для первого PDF
            byte[] firstPdfBytes = generateAndStoreQRCodeWithTextForBox(boxes.get(0).getId());
            merger.addSource(new ByteArrayInputStream(firstPdfBytes));

            // Добавляем остальные PDF-документы
            for (int i = 1; i < boxes.size(); i++) {
                byte[] pdfBytes = generateAndStoreQRCodeWithTextForBox(boxes.get(i).getId());
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
        List<Box> boxes = boxRepository.findByProductBatch_Id(batchId);
        if (boxes.isEmpty()) {
            throw new IllegalArgumentException("No boxes found for product batch id " + batchId);
        }
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
        List<Box> boxes = boxRepository.findByProductBatch_Id(batchId);
        if (boxes.isEmpty()) {
            throw new IllegalArgumentException("No boxes found for product batch id " + batchId);
        }
        return boxes.get(0).getId();
    }
}