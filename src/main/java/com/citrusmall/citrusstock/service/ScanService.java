package com.citrusmall.citrusstock.service;

import com.citrusmall.citrusstock.model.*;
import com.citrusmall.citrusstock.model.enums.GoodsStatus;
import com.citrusmall.citrusstock.model.enums.ScanMode;
import com.citrusmall.citrusstock.repository.BoxRepository;
import com.citrusmall.citrusstock.repository.ScanEventRepository;
import com.citrusmall.citrusstock.repository.UserRepository;
import com.citrusmall.citrusstock.repository.ZoneRepository;
import com.citrusmall.citrusstock.util.QRCodeContentBuilder;
import com.citrusmall.citrusstock.util.codegen.CodeGeneratorFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.time.LocalDateTime;
import java.util.Base64;
import java.util.List;

@Service
public class ScanService {

    @Autowired
    private BoxRepository boxRepository;

    @Autowired
    private ScanEventRepository scanEventRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ProductBatchService productBatchService;

    @Autowired
    private ZoneRepository zoneRepository;

    @Autowired
    private CodeGeneratorFactory codeGeneratorFactory;

    /**
     * Генерирует QR-код для коробки и сохраняет его в поле code,
     * если QR-код ещё не сгенерирован.
     * Используется метод QRCodeContentBuilder для формирования содержимого.
     *
     * @param box коробка, для которой генерируется QR-код
     * @throws Exception в случае ошибки генерации
     */
    private void generateAndStoreQRCodeIfNeeded(Box box) throws Exception {
        if (box.getCode() == null || box.getCode().isEmpty()) {
            String qrContent = QRCodeContentBuilder.buildContent(box);
            BufferedImage qrImage = codeGeneratorFactory.getGenerator("QR").generateCodeImage(qrContent, 200, 200);
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            ImageIO.write(qrImage, "png", baos);
            String base64Image = Base64.getEncoder().encodeToString(baos.toByteArray());
            box.setCode(base64Image);
        }
    }

    public void scanBoxByMode(Long boxId, Long userId, ScanMode scanMode) throws Exception {
        if (scanMode == ScanMode.ON_WAREHOUSE) {
            scanNewBox(boxId, userId);
        } else if (scanMode == ScanMode.SHIPMENT) {
            scanBoxForShipment(boxId, userId);
        } else {
            throw new IllegalArgumentException("Unsupported scan mode: " + scanMode);
        }
    }


    /**
     * Универсальный метод сканирования коробки.
     * Обновляет статус коробки, генерирует QR-код (если ещё не сгенерирован),
     * создаёт событие сканирования и проверяет необходимость обновления статуса партии.
     *
     * @param boxId           ID сканируемой коробки
     * @param userId          ID оператора, выполнившего сканирование
     * @param targetZoneName  имя целевой зоны (RECEIVING для новых товаров, SHIPMENT для отгрузки)
     * @param newBatchStatus  новый статус партии (параметр сохранен для обратной совместимости)
     * @param newBoxStatus    новый статус коробки (SCANNED для новых товаров, SHIPPED для отгрузки)
     * @throws Exception      в случае ошибки генерации QR-кода или сохранения данных
     */
    public void scanBox(Long boxId, Long userId, String targetZoneName, GoodsStatus newBatchStatus, GoodsStatus newBoxStatus) throws Exception {
        // 1. Находим коробку и пользователя
        Box box = boxRepository.findById(boxId)
                .orElseThrow(() -> new IllegalArgumentException("Box with id " + boxId + " not found"));
        
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("User not found with id " + userId));

        // 2. Генерируем QR-код, если необходимо
        generateAndStoreQRCodeIfNeeded(box);

        // 3. Обновляем статус и информацию о сканировании коробки
        box.setStatus(newBoxStatus);
        box.setScannedAt(LocalDateTime.now());
        box.setScannedBy(user);
        boxRepository.save(box);

        // 4. Создаем событие сканирования
        ScanMode mode = targetZoneName.equalsIgnoreCase("SHIPMENT") ? ScanMode.SHIPMENT : ScanMode.ON_WAREHOUSE;
        createScanEvent(box, user, mode);

        // 5. Получаем партию и целевую зону
        ProductBatch batch = box.getProductBatch();
        Zone targetZone = zoneRepository.findByName(targetZoneName)
                .orElseThrow(() -> new IllegalStateException("Zone '" + targetZoneName + "' not found"));
                
        // 6. Обновляем статус партии на основе статусов всех её коробок
        productBatchService.updateBatchStatusBasedOnBoxes(batch.getId(), targetZone);
    }
    
    /**
     * Создает и сохраняет событие сканирования для коробки
     *
     * @param box  отсканированная коробка
     * @param user пользователь, выполнивший сканирование
     * @param mode режим сканирования
     */
    private void createScanEvent(Box box, User user, ScanMode mode) {
        ScanEvent event = new ScanEvent();
        event.setBox(box);
        event.setUser(user);
        event.setScanMode(mode);
        event.setScanTime(LocalDateTime.now());
        scanEventRepository.save(event);
    }

    /**
     * Сканирование новых товаров.
     * При сканировании новых товаров устанавливаем:
     *  - Статус коробки: SCANNED
     *  - Статус партии: CONFIRMED
     *
     * @param boxId  ID коробки
     * @param userId ID оператора
     * @throws Exception
     */
    public void scanNewBox(Long boxId, Long userId) throws Exception {
        // Для новых товаров используем default зону "RECEIVING"
        scanBox(boxId, userId, "RECEIVING", GoodsStatus.SCANNED, GoodsStatus.SCANNED);
    }

    /**
     * Сканирование коробки при отгрузке.
     * При сканировании для отгрузки устанавливаем:
     *  - Статус коробки: SHIPPED
     *  - Статус партии: SHIPPED
     *  - Зона партии: SHIPMENT
     *
     * @param boxId  ID коробки
     * @param userId ID оператора
     * @throws Exception
     */
    public void scanBoxForShipment(Long boxId, Long userId) throws Exception {
        // Для отгрузки используем default зону "SHIPMENT"
        scanBox(boxId, userId, "SHIPMENT", GoodsStatus.SHIPPED, GoodsStatus.SHIPPED);
    }
}