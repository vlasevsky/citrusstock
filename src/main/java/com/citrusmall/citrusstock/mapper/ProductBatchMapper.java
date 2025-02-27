package com.citrusmall.citrusstock.mapper;

import com.citrusmall.citrusstock.dto.ProductBatchCreateRequest;
import com.citrusmall.citrusstock.dto.ProductBatchResponse;
import com.citrusmall.citrusstock.model.ProductBatch;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring", uses = {ProductMapper.class, SupplierMapper.class, BoxMapper.class})
public interface ProductBatchMapper {

    // При создании поле zone игнорируется, оно будет установлено в сервисе, если не передано
    @Mapping(target = "receivedAt", expression = "java(request.getReceivedAt() != null ? request.getReceivedAt() : java.time.LocalDateTime.now())")
    @Mapping(target = "status", constant = "GENERATED")
    @Mapping(target = "zone", ignore = true)
    @Mapping(target = "boxes", ignore = true)
    ProductBatch toProductBatch(ProductBatchCreateRequest request);

    // Для ответа маппим productId, supplierId и извлекаем id зоны
    @Mapping(source = "product.id", target = "productId")
    @Mapping(source = "supplier.id", target = "supplierId")
    @Mapping(expression = "java(productBatch.getStatus().name())", target = "status")
    @Mapping(expression = "java(productBatch.getZone() != null ? productBatch.getZone().getId() : null)", target = "zone")
    ProductBatchResponse toProductBatchResponse(ProductBatch productBatch);
}
