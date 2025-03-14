package com.citrusmall.citrusstock.service;

import com.citrusmall.citrusstock.model.Box;
import com.citrusmall.citrusstock.model.ProductBatch;
import com.citrusmall.citrusstock.model.Zone;
import com.citrusmall.citrusstock.model.enums.GoodsStatus;
import com.citrusmall.citrusstock.repository.BoxRepository;
import com.citrusmall.citrusstock.repository.ProductBatchRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@DisplayName("Тесты ProductBatchService")
class ProductBatchServiceTest {

    @Mock
    private ProductBatchRepository productBatchRepository;

    @Mock
    private BoxRepository boxRepository;

    @InjectMocks
    private ProductBatchService productBatchService;

    private ProductBatch productBatch;
    private Zone zone;
    private List<Box> boxes;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);

        // Создаем тестовые объекты
        productBatch = new ProductBatch();
        productBatch.setId(1L);
        productBatch.setStatus(GoodsStatus.GENERATED);

        zone = new Zone();
        zone.setId(1L);
        zone.setName("TEST_ZONE");

        // Создаем тестовые коробки
        Box box1 = new Box();
        box1.setId(1L);
        box1.setProductBatch(productBatch);

        Box box2 = new Box();
        box2.setId(2L);
        box2.setProductBatch(productBatch);

        Box box3 = new Box();
        box3.setId(3L);
        box3.setProductBatch(productBatch);

        boxes = Arrays.asList(box1, box2, box3);
    }

    @Nested
    @DisplayName("Тесты обновления статуса партии на основе статусов коробок")
    class UpdateBatchStatusBasedOnBoxesTests {

        @Test
        @DisplayName("Если все коробки SCANNED, то и партия становится SCANNED")
        void whenAllBoxesAreScanned_thenBatchStatusIsUpdatedToScanned() {
            // Arrange
            Long batchId = 1L;
            // Устанавливаем статус SCANNED всем коробкам
            boxes.forEach(box -> box.setStatus(GoodsStatus.SCANNED));

            when(productBatchRepository.findById(batchId)).thenReturn(Optional.of(productBatch));
            when(boxRepository.findByProductBatch_Id(batchId)).thenReturn(boxes);

            // Act
            productBatchService.updateBatchStatusBasedOnBoxes(batchId, zone);

            // Assert
            assertEquals(GoodsStatus.SCANNED, productBatch.getStatus(), "Статус партии должен быть SCANNED");
            assertEquals(zone, productBatch.getZone(), "Зона партии должна быть обновлена");
            verify(productBatchRepository).save(productBatch);
        }

        @Test
        @DisplayName("Если все коробки SHIPPED, то и партия становится SHIPPED")
        void whenAllBoxesAreShipped_thenBatchStatusIsUpdatedToShipped() {
            // Arrange
            Long batchId = 1L;
            // Устанавливаем статус SHIPPED всем коробкам
            boxes.forEach(box -> box.setStatus(GoodsStatus.SHIPPED));

            when(productBatchRepository.findById(batchId)).thenReturn(Optional.of(productBatch));
            when(boxRepository.findByProductBatch_Id(batchId)).thenReturn(boxes);

            // Act
            productBatchService.updateBatchStatusBasedOnBoxes(batchId, zone);

            // Assert
            assertEquals(GoodsStatus.SHIPPED, productBatch.getStatus(), "Статус партии должен быть SHIPPED");
            assertEquals(zone, productBatch.getZone(), "Зона партии должна быть обновлена");
            verify(productBatchRepository).save(productBatch);
        }

        @Test
        @DisplayName("Если коробки имеют разные статусы, статус партии не меняется")
        void whenBoxesHaveMixedStatuses_thenBatchStatusIsNotChanged() {
            // Arrange
            Long batchId = 1L;
            // Устанавливаем разные статусы
            boxes.get(0).setStatus(GoodsStatus.SCANNED);
            boxes.get(1).setStatus(GoodsStatus.SHIPPED);
            boxes.get(2).setStatus(GoodsStatus.GENERATED);

            GoodsStatus originalStatus = productBatch.getStatus();

            when(productBatchRepository.findById(batchId)).thenReturn(Optional.of(productBatch));
            when(boxRepository.findByProductBatch_Id(batchId)).thenReturn(boxes);

            // Act
            productBatchService.updateBatchStatusBasedOnBoxes(batchId, zone);

            // Assert
            assertEquals(originalStatus, productBatch.getStatus(), "Статус партии не должен измениться");
            verify(productBatchRepository, never()).save(any(ProductBatch.class));
        }

        @Test
        @DisplayName("Если у партии нет коробок, статус партии не меняется")
        void whenBatchHasNoBoxes_thenBatchStatusIsNotChanged() {
            // Arrange
            Long batchId = 1L;
            GoodsStatus originalStatus = productBatch.getStatus();

            when(productBatchRepository.findById(batchId)).thenReturn(Optional.of(productBatch));
            when(boxRepository.findByProductBatch_Id(batchId)).thenReturn(Collections.emptyList());

            // Act
            productBatchService.updateBatchStatusBasedOnBoxes(batchId, zone);

            // Assert
            assertEquals(originalStatus, productBatch.getStatus(), "Статус партии не должен измениться");
            verify(productBatchRepository, never()).save(any(ProductBatch.class));
        }
    }

    @Nested
    @DisplayName("Тесты поиска партий с коробками в разных статусах")
    class FindBatchesWithMixedBoxStatusesTests {

        @Test
        @DisplayName("Находит партии с коробками в разных статусах")
        void returnsOnlyBatchesWithMixedBoxStatuses() {
            // Arrange
            ProductBatch batch1 = new ProductBatch();
            batch1.setId(1L);

            ProductBatch batch2 = new ProductBatch();
            batch2.setId(2L);

            // Создаем коробки с одинаковым статусом для первой партии
            Box box1 = new Box();
            box1.setStatus(GoodsStatus.SCANNED);
            
            Box box2 = new Box();
            box2.setStatus(GoodsStatus.SCANNED);

            // Создаем коробки с разными статусами для второй партии
            Box box3 = new Box();
            box3.setStatus(GoodsStatus.SCANNED);
            
            Box box4 = new Box();
            box4.setStatus(GoodsStatus.SHIPPED);

            when(productBatchRepository.findAll()).thenReturn(Arrays.asList(batch1, batch2));
            when(boxRepository.findByProductBatch_Id(1L)).thenReturn(Arrays.asList(box1, box2));
            when(boxRepository.findByProductBatch_Id(2L)).thenReturn(Arrays.asList(box3, box4));

            // Act
            List<ProductBatch> result = productBatchService.findBatchesWithMixedBoxStatuses();

            // Assert
            assertEquals(1, result.size(), "Должна быть найдена только одна партия");
            assertEquals(batch2.getId(), result.get(0).getId(), "Должна быть найдена партия с ID=2");
        }

        @Test
        @DisplayName("Возвращает пустой список, если нет партий с коробками в разных статусах")
        void returnsEmptyListWhenNoMixedStatuses() {
            // Arrange
            ProductBatch batch1 = new ProductBatch();
            batch1.setId(1L);

            ProductBatch batch2 = new ProductBatch();
            batch2.setId(2L);

            // Все коробки имеют одинаковый статус в своих партиях
            Box box1 = new Box();
            box1.setStatus(GoodsStatus.SCANNED);
            
            Box box2 = new Box();
            box2.setStatus(GoodsStatus.SCANNED);
            
            Box box3 = new Box();
            box3.setStatus(GoodsStatus.SHIPPED);
            
            Box box4 = new Box();
            box4.setStatus(GoodsStatus.SHIPPED);

            when(productBatchRepository.findAll()).thenReturn(Arrays.asList(batch1, batch2));
            when(boxRepository.findByProductBatch_Id(1L)).thenReturn(Arrays.asList(box1, box2));
            when(boxRepository.findByProductBatch_Id(2L)).thenReturn(Arrays.asList(box3, box4));

            // Act
            List<ProductBatch> result = productBatchService.findBatchesWithMixedBoxStatuses();

            // Assert
            assertTrue(result.isEmpty(), "Список должен быть пустым");
        }
    }

    @Nested
    @DisplayName("Тесты получения статистики статусов коробок")
    class BoxStatusStatisticsTests {

        @Test
        @DisplayName("Возвращает корректную статистику по статусам коробок")
        void returnsCorrectStatistics() {
            // Arrange
            Long batchId = 1L;
            boxes.get(0).setStatus(GoodsStatus.SCANNED);
            boxes.get(1).setStatus(GoodsStatus.SCANNED);
            boxes.get(2).setStatus(GoodsStatus.SHIPPED);

            when(boxRepository.findByProductBatch_Id(batchId)).thenReturn(boxes);

            // Act
            Map<GoodsStatus, Long> result = productBatchService.getBoxStatusStatisticsForBatch(batchId);

            // Assert
            assertEquals(2, result.size(), "Должно быть два разных статуса");
            assertEquals(2L, result.get(GoodsStatus.SCANNED), "Должно быть 2 коробки со статусом SCANNED");
            assertEquals(1L, result.get(GoodsStatus.SHIPPED), "Должна быть 1 коробка со статусом SHIPPED");
        }

        @Test
        @DisplayName("Возвращает пустую статистику, если у партии нет коробок")
        void returnsEmptyStatisticsWhenNoBoxes() {
            // Arrange
            Long batchId = 1L;
            when(boxRepository.findByProductBatch_Id(batchId)).thenReturn(Collections.emptyList());

            // Act
            Map<GoodsStatus, Long> result = productBatchService.getBoxStatusStatisticsForBatch(batchId);

            // Assert
            assertTrue(result.isEmpty(), "Статистика должна быть пустой");
        }
    }
} 