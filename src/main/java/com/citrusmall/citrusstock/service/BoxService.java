package com.citrusmall.citrusstock.service;

import com.citrusmall.citrusstock.dto.BoxCreateRequest;
import com.citrusmall.citrusstock.model.Box;
import com.citrusmall.citrusstock.model.ProductBatch;
import com.citrusmall.citrusstock.model.enums.GoodsStatus;
import com.citrusmall.citrusstock.repository.BoxRepository;
import com.citrusmall.citrusstock.repository.ProductBatchRepository;
import com.citrusmall.citrusstock.util.QRCodeContentBuilder;
import com.citrusmall.citrusstock.util.codegen.CodeGenerator;
import com.citrusmall.citrusstock.util.codegen.CodeGeneratorFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Base64;
import java.util.List;

@Service
public class BoxService {

    @Autowired
    private BoxRepository boxRepository;

    @Autowired
    private ProductBatchRepository productBatchRepository;
    
    @Autowired
    private CodeGeneratorFactory codeGeneratorFactory;

    /**
     * Создает новую коробку для указанной партии.
     * После создания генерирует QR-код, если partii уже имеет ID и сохранена в БД.
     *
     * @param request запрос на создание коробки
     * @return созданная коробка с QR-кодом
     */
    public Box createBox(BoxCreateRequest request) {
        Box box = new Box();
        ProductBatch batch = productBatchRepository.findById(request.getProductBatchId())
                .orElseThrow(() -> new IllegalArgumentException("ProductBatch not found with id " + request.getProductBatchId()));
        box.setProductBatch(batch);
        box.setStatus(GoodsStatus.GENERATED);
        
        // Сначала сохраняем коробку чтобы она получила ID
        box = boxRepository.save(box);
        
        // Пытаемся сгенерировать QR-код сразу после создания
        try {
            generateQRCodeIfNeeded(box);
            box = boxRepository.save(box); // Сохраняем с обновленным QR-кодом
        } catch (Exception e) {
            // Логируем ошибку генерации QR-кода, но не прерываем работу
            System.err.println("Failed to generate QR code for box " + box.getId() + ": " + e.getMessage());
        }
        
        return box;
    }

    /**
     * Получает коробку по ID.
     *
     * @param id идентификатор коробки
     * @return найденная коробка
     */
    public Box getBoxById(Long id) {
        return boxRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Box not found with id " + id));
    }

    /**
     * Генерирует QR-код для коробки, если он еще не был сгенерирован.
     *
     * @param box коробка, для которой генерируется QR-код
     * @return true если QR-код был сгенерирован, false если он уже существовал
     * @throws Exception в случае ошибки генерации
     */
    public boolean generateQRCodeIfNeeded(Box box) throws Exception {
        if (box.getCode() == null || box.getCode().isEmpty()) {
            String qrContent = QRCodeContentBuilder.buildContent(box);
            CodeGenerator generator = codeGeneratorFactory.getGenerator("QR");
            byte[] qrImageBytes = generator.generateCodeImage(qrContent, 200, 200);
            String base64Image = Base64.getEncoder().encodeToString(qrImageBytes);
            box.setCode(base64Image);
            return true;
        }
        return false;
    }

    public List<Box> getAllBoxes() {
        return boxRepository.findAll();
    }

    public Box updateBox(Long id, Box boxDetails) {
        Box box = getBoxById(id);
        box.setCode(boxDetails.getCode());
        box.setStatus(boxDetails.getStatus());
        box.setScannedAt(boxDetails.getScannedAt());
        box.setScannedBy(boxDetails.getScannedBy());
        return boxRepository.save(box);
    }

    public void deleteBox(Long id) {
        boxRepository.deleteById(id);
    }

    public List<Box> getBoxesByProductBatchId(Long batchId) {
        return boxRepository.findByProductBatch_Id(batchId);
    }
}