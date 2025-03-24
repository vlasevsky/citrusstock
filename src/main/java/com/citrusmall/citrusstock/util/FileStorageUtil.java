package com.citrusmall.citrusstock.util;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

/**
 * Утилитный класс для работы с файловой системой.
 * Инкапсулирует логику сохранения файлов в определенные директории.
 */
public class FileStorageUtil {

    private static final String QR_CODES_BASE_DIR = "qr/codes";

    /**
     * Сохраняет файл в директорию QR-кодов для указанного продукта.
     *
     * @param productId ID продукта
     * @param fileName имя файла
     * @param fileContent содержимое файла
     * @throws IOException если возникла ошибка ввода-вывода
     */
    public static void saveQrCodeFile(Long productId, String fileName, byte[] fileContent) throws IOException {
        Path productFolder = getOrCreateProductFolder(productId);
        Path filePath = productFolder.resolve(fileName);
        Files.write(filePath, fileContent);
    }

    /**
     * Получает или создает директорию для QR-кодов продукта.
     *
     * @param productId ID продукта
     * @return путь к директории
     * @throws IOException если возникла ошибка при создании директории
     */
    public static Path getOrCreateProductFolder(Long productId) throws IOException {
        String productFolderName = "product_" + productId;
        Path storageBasePath = Paths.get(QR_CODES_BASE_DIR);
        Path productFolder = storageBasePath.resolve(productFolderName);
        
        if (!Files.exists(productFolder)) {
            Files.createDirectories(productFolder);
        }
        
        return productFolder;
    }

    /**
     * Генерирует имя файла PDF для QR-кода коробки.
     *
     * @param boxId ID коробки
     * @return имя файла
     */
    public static String generateQrCodePdfFileName(Long boxId) {
        return "qr_box_" + boxId + ".pdf";
    }
} 