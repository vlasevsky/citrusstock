package com.citrusmall.citrusstock.mapper;

import com.citrusmall.citrusstock.dto.SupplierCreateRequest;
import com.citrusmall.citrusstock.dto.SupplierResponse;
import com.citrusmall.citrusstock.model.Supplier;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface SupplierMapper {
    SupplierResponse toSupplierResponse(Supplier supplier);
    Supplier toSupplier(SupplierCreateRequest request);
}