package com.citrusmall.citrusstock.service;

import com.citrusmall.citrusstock.model.Box;
import com.citrusmall.citrusstock.model.ProductBatch;
import com.citrusmall.citrusstock.model.ScanEvent;
import com.citrusmall.citrusstock.model.User;
import com.citrusmall.citrusstock.model.enums.BoxStatus;
import com.citrusmall.citrusstock.model.enums.ProductBatchStatus;
import com.citrusmall.citrusstock.model.enums.ScanMode;
import com.citrusmall.citrusstock.model.enums.Zone;
import com.citrusmall.citrusstock.repository.BoxRepository;
import com.citrusmall.citrusstock.repository.ScanEventRepository;
import com.citrusmall.citrusstock.repository.UserRepository;
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

    /**
     * Универсальный метод сканирования коробки.
     * Обновляет статус коробки, генерирует QR-код (если ещё не сгенерирован),
     * создаёт событие сканирования и, если все коробки партии имеют нужный статус,
     * обновляет статус и зону партии.
     *
     * @param boxId           ID сканируемой коробки
     * @param userId          ID оператора, выполнившего сканирование
     * @param targetZone      новая зона партии (STORAGE для новых товаров, SHIPMENT для отгрузки)
     * @param newBatchStatus  новый статус партии (CONFIRMED для новых товаров, SHIPPED для отгрузки)
     * @param newBoxStatus    новый статус коробки (SCANNED для новых товаров, SHIPPED для отгрузки)
     * @throws Exception      в случае ошибки генерации QR-кода или сохранения данных
     */
    public void scanBox(Long boxId, Long userId, Zone targetZone, ProductBatchStatus newBatchStatus, BoxStatus newBoxStatus) throws Exception {
        Box box = boxRepository.findById(boxId)
                .orElseThrow(() -> new IllegalArgumentException("Box with id " + boxId + " not found"));

        // Если QR-код ещё не сгенерирован, генерируем и сохраняем его
        generateAndStoreQRCodeIfNeeded(box);

        // Обновляем статус коробки и время сканирования
        box.setStatus(newBoxStatus);
        box.setScannedAt(LocalDateTime.now());
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("User not found with id " + userId));
        box.setScannedBy(user);
        boxRepository.save(box);

        // Определяем режим сканирования
        ScanMode mode = (targetZone == Zone.SHIPMENT) ? ScanMode.SHIPMENT : ScanMode.ON_WAREHOUSE;

        // Создаём событие сканирования
        ScanEvent event = new ScanEvent();
        event.setBox(box);
        event.setUser(user);
        event.setScanMode(mode);
        event.setScanTime(LocalDateTime.now());
        scanEventRepository.save(event);

        // Проверяем, что все коробки партии имеют нужный статус, и обновляем статус и зону партии
        ProductBatch batch = box.getProductBatch();
        List<Box> boxes = boxRepository.findByProductBatch_Id(batch.getId());
        boolean allMatch = boxes.stream().allMatch(b -> b.getStatus() == newBoxStatus);
        if (allMatch) {
            productBatchService.updateBatchStatus(batch.getId(), newBatchStatus, targetZone);
        }
    }

    /**
     * Сканирование новых товаров.
     * При сканировании новых товаров устанавливаем:
     *  - Статус коробки: SCANNED
     *  - Статус партии: CONFIRMED
     *  - Зона партии: STORAGE
     *
     * @param boxId  ID коробки
     * @param userId ID оператора
     * @throws Exception
     */
    public void scanNewBox(Long boxId, Long userId) throws Exception {
        scanBox(boxId, userId, Zone.STORAGE, ProductBatchStatus.CONFIRMED, BoxStatus.SCANNED);
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
        scanBox(boxId, userId, Zone.SHIPMENT, ProductBatchStatus.SHIPPED, BoxStatus.SHIPPED);
    }
}