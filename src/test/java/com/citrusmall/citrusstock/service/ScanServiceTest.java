package com.citrusmall.citrusstock.service;

import com.citrusmall.citrusstock.model.*;
import com.citrusmall.citrusstock.model.enums.GoodsStatus;
import com.citrusmall.citrusstock.model.enums.ScanMode;
import com.citrusmall.citrusstock.repository.*;
import com.citrusmall.citrusstock.util.codegen.CodeGeneratorFactory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.time.LocalDateTime;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@DisplayName("Тесты ScanService")
class ScanServiceTest {

    @Mock
    private BoxRepository boxRepository;

    @Mock
    private ScanEventRepository scanEventRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private ProductBatchService productBatchService;

    @Mock
    private ZoneRepository zoneRepository;

    @Mock
    private CodeGeneratorFactory codeGeneratorFactory;

    @InjectMocks
    private ScanService scanService;

    private Box box;
    private User user;
    private Zone zone;
    private ProductBatch productBatch;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);

        // Создаем тестовые объекты
        user = new User();
        user.setId(1L);

        zone = new Zone();
        zone.setId(1L);
        zone.setName("RECEIVING");

        productBatch = new ProductBatch();
        productBatch.setId(1L);
        productBatch.setStatus(GoodsStatus.GENERATED);

        box = new Box();
        box.setId(1L);
        box.setProductBatch(productBatch);
        box.setStatus(GoodsStatus.GENERATED);
    }

    @Nested
    @DisplayName("Тесты метода scanBox")
    class ScanBoxTests {

        @Test
        @DisplayName("Метод scanBox обновляет статус коробки и вызывает обновление статуса партии")
        void scanBox_updatesBoxStatusAndCallsBatchStatusUpdate() throws Exception {
            // Arrange
            Long boxId = 1L;
            Long userId = 1L;
            String targetZoneName = "RECEIVING";
            GoodsStatus newBatchStatus = GoodsStatus.SCANNED; // Для обратной совместимости
            GoodsStatus newBoxStatus = GoodsStatus.SCANNED;

            when(boxRepository.findById(boxId)).thenReturn(Optional.of(box));
            when(userRepository.findById(userId)).thenReturn(Optional.of(user));
            when(zoneRepository.findByName(targetZoneName)).thenReturn(Optional.of(zone));
            when(boxRepository.save(any(Box.class))).thenReturn(box);
            when(scanEventRepository.save(any(ScanEvent.class))).thenReturn(new ScanEvent());

            // Act
            scanService.scanBox(boxId, userId, targetZoneName, newBatchStatus, newBoxStatus);

            // Assert
            // Проверяем, что метод ищет коробку по ID
            verify(boxRepository).findById(boxId);
            
            // Проверяем, что статус коробки обновлен
            assertEquals(GoodsStatus.SCANNED, box.getStatus());
            verify(box).setScannedAt(any(LocalDateTime.class));
            verify(box).setScannedBy(user);
            verify(boxRepository).save(box);
            
            // Проверяем, что создано событие сканирования
            verify(scanEventRepository).save(any(ScanEvent.class));
            
            // Проверяем, что вызван метод обновления статуса партии с правильными параметрами
            verify(productBatchService).updateBatchStatusBasedOnBoxes(productBatch.getId(), zone);
        }
    }

    @Nested
    @DisplayName("Тесты специализированных методов сканирования")
    class SpecializedScanMethodsTests {

        @Test
        @DisplayName("scanNewBox вызывает scanBox с параметрами для новых товаров")
        void scanNewBox_callsScanBoxWithParametersForNewGoods() throws Exception {
            // Arrange
            Long boxId = 1L;
            Long userId = 1L;

            // Мокируем scanBox, так как мы тестировали его выше
            doNothing().when(scanService).scanBox(
                    anyLong(),
                    anyLong(),
                    anyString(),
                    any(GoodsStatus.class),
                    any(GoodsStatus.class)
            );

            // Act
            scanService.scanNewBox(boxId, userId);

            // Assert
            verify(scanService).scanBox(
                    boxId,
                    userId,
                    "RECEIVING",
                    GoodsStatus.SCANNED,
                    GoodsStatus.SCANNED
            );
        }

        @Test
        @DisplayName("scanBoxForShipment вызывает scanBox с параметрами для отгрузки")
        void scanBoxForShipment_callsScanBoxWithParametersForShipment() throws Exception {
            // Arrange
            Long boxId = 1L;
            Long userId = 1L;

            // Мокируем scanBox, так как мы тестировали его выше
            doNothing().when(scanService).scanBox(
                    anyLong(),
                    anyLong(),
                    anyString(),
                    any(GoodsStatus.class),
                    any(GoodsStatus.class)
            );

            // Act
            scanService.scanBoxForShipment(boxId, userId);

            // Assert
            verify(scanService).scanBox(
                    boxId,
                    userId,
                    "SHIPMENT",
                    GoodsStatus.SHIPPED,
                    GoodsStatus.SHIPPED
            );
        }
    }

    @Nested
    @DisplayName("Тесты метода scanBoxByMode")
    class ScanBoxByModeTests {

        @Test
        @DisplayName("Вызывает scanNewBox для режима ON_WAREHOUSE")
        void callsScanNewBoxForOnWarehouseMode() throws Exception {
            // Arrange
            Long boxId = 1L;
            Long userId = 1L;

            // Мокируем соответствующие методы
            doNothing().when(scanService).scanNewBox(anyLong(), anyLong());
            doNothing().when(scanService).scanBoxForShipment(anyLong(), anyLong());

            // Act
            scanService.scanBoxByMode(boxId, userId, ScanMode.ON_WAREHOUSE);

            // Assert
            verify(scanService).scanNewBox(boxId, userId);
            verify(scanService, never()).scanBoxForShipment(anyLong(), anyLong());
        }

        @Test
        @DisplayName("Вызывает scanBoxForShipment для режима SHIPMENT")
        void callsScanBoxForShipmentForShipmentMode() throws Exception {
            // Arrange
            Long boxId = 1L;
            Long userId = 1L;

            // Мокируем соответствующие методы
            doNothing().when(scanService).scanNewBox(anyLong(), anyLong());
            doNothing().when(scanService).scanBoxForShipment(anyLong(), anyLong());

            // Act
            scanService.scanBoxByMode(boxId, userId, ScanMode.SHIPMENT);

            // Assert
            verify(scanService, never()).scanNewBox(anyLong(), anyLong());
            verify(scanService).scanBoxForShipment(boxId, userId);
        }

        @Test
        @DisplayName("Выбрасывает исключение для неподдерживаемого режима")
        void throwsExceptionForUnsupportedMode() {
            // Arrange
            Long boxId = 1L;
            Long userId = 1L;
            ScanMode unsupportedMode = null; // Неподдерживаемый режим

            // Act & Assert
            try {
                scanService.scanBoxByMode(boxId, userId, unsupportedMode);
            } catch (Exception e) {
                assertEquals(IllegalArgumentException.class, e.getClass());
                assertEquals("Unsupported scan mode: " + unsupportedMode, e.getMessage());
            }
        }
    }
} 