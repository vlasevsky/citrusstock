package com.citrusmall.citrusstock.mapper;

import com.citrusmall.citrusstock.dto.BoxCreateRequest;
import com.citrusmall.citrusstock.dto.BoxResponse;
import com.citrusmall.citrusstock.model.Box;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring", uses = {UserMapper.class})
public interface BoxMapper {


    @Mapping(source = "productBatch.id", target = "productBatchId")
    @Mapping(expression = "java(box.getStatus().name())", target = "status")
    BoxResponse toBoxResponse(Box box);

    // Преобразование входящего DTO для создания коробки в сущность Box.
    // Здесь мы устанавливаем статус по умолчанию как GENERATED.
    @Mapping(target = "status", constant = "GENERATED")
    Box toBox(BoxCreateRequest request);
}
