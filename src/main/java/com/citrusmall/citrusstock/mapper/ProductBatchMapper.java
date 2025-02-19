package com.citrusmall.citrusstock.mapper;

import com.citrusmall.citrusstock.dto.ProductBatchCreateRequest;
import com.citrusmall.citrusstock.dto.ProductBatchResponse;
import com.citrusmall.citrusstock.model.ProductBatch;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring", uses = {ProductMapper.class, SupplierMapper.class, BoxMapper.class})
public interface ProductBatchMapper {
    // Преобразование входящего DTO в сущность ProductBatch.
    // Поле receivedAt задается текущим временем, если не передано, статус и зона устанавливаются по умолчанию.
    @Mapping(target = "receivedAt", expression = "java(request.getReceivedAt() != null ? request.getReceivedAt() : java.time.LocalDateTime.now())")
    @Mapping(target = "status", constant = "REGISTERED")
    @Mapping(target = "zone", constant = "RECEIVING")
    ProductBatch toProductBatch(ProductBatchCreateRequest request);

    // Преобразование сущности ProductBatch в DTO для ответа.
    @Mapping(source = "product.id", target = "productId")
    @Mapping(source = "supplier.id", target = "supplierId")
    @Mapping(expression = "java(productBatch.getStatus().name())", target = "status")
    @Mapping(expression = "java(productBatch.getZone().name())", target = "zone")
    ProductBatchResponse toProductBatchResponse(ProductBatch productBatch);
}
