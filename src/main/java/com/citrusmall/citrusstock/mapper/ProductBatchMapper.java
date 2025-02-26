package com.citrusmall.citrusstock.mapper;

import com.citrusmall.citrusstock.dto.ProductBatchCreateRequest;
import com.citrusmall.citrusstock.dto.ProductBatchResponse;
import com.citrusmall.citrusstock.model.ProductBatch;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring", uses = {ProductMapper.class, SupplierMapper.class, BoxMapper.class})
public interface ProductBatchMapper {

    // При создании ProductBatch поле zone игнорируется (будет установлено дефолтно)
    @Mapping(target = "receivedAt", expression = "java(request.getReceivedAt() != null ? request.getReceivedAt() : java.time.LocalDateTime.now())")
    @Mapping(target = "status", constant = "GENERATED")
    @Mapping(target = "zone", ignore = true)
    ProductBatch toProductBatch(ProductBatchCreateRequest request);

    // Для ответа маппим productId, supplierId и извлекаем имя зоны
    @Mapping(source = "product.id", target = "productId")
    @Mapping(source = "supplier.id", target = "supplierId")
    @Mapping(expression = "java(productBatch.getStatus().name())", target = "status")
    @Mapping(expression = "java(productBatch.getZone() != null ? productBatch.getZone().getName() : null)", target = "zone")
    ProductBatchResponse toProductBatchResponse(ProductBatch productBatch);
}
