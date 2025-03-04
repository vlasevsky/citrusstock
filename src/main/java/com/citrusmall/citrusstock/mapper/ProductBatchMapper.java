package com.citrusmall.citrusstock.mapper;

import com.citrusmall.citrusstock.dto.ProductBatchCreateRequest;
import com.citrusmall.citrusstock.dto.ProductBatchResponse;
import com.citrusmall.citrusstock.model.ProductBatch;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(
        componentModel = "spring",
        imports = { com.citrusmall.citrusstock.util.EnumLocalizer.class },
        uses = {ProductMapper.class, SupplierMapper.class, BoxMapper.class, ZoneMapper.class}
)
public interface ProductBatchMapper {

    @Mapping(target = "receivedAt", expression = "java(request.getReceivedAt() != null ? request.getReceivedAt() : java.time.LocalDateTime.now())")
    @Mapping(target = "status", constant = "GENERATED")
    @Mapping(target = "zone", ignore = true)
    @Mapping(target = "boxes", ignore = true)
    ProductBatch toProductBatch(ProductBatchCreateRequest request);

    // Маппинг с использованием вложенных мапперов для product, supplier и zone
    @Mapping(source = "product", target = "product")
    @Mapping(source = "supplier", target = "supplier")
    @Mapping(expression = "java(EnumLocalizer.localizeGoodsStatus(productBatch.getStatus()))", target = "status")
    // Передаем объект zone целиком, а его локализацию выполнит ZoneMapper
    @Mapping(source = "zone", target = "zone")
    ProductBatchResponse toProductBatchResponse(ProductBatch productBatch);
}
