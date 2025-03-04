package com.citrusmall.citrusstock.mapper;

import com.citrusmall.citrusstock.dto.BoxCreateRequest;
import com.citrusmall.citrusstock.dto.BoxResponse;
import com.citrusmall.citrusstock.model.Box;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring", uses = {UserMapper.class},
        imports = {com.citrusmall.citrusstock.util.EnumLocalizer.class})
public interface BoxMapper {


    @Mapping(target = "status", constant = "GENERATED")
    Box toBox(BoxCreateRequest request);

    @Mapping(source = "productBatch.id", target = "productBatchId")
    @Mapping(expression = "java(EnumLocalizer.localizeGoodsStatus(box.getStatus()))", target = "status")
    BoxResponse toBoxResponse(Box box);
}
