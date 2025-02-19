package com.citrusmall.citrusstock.mapper;

import com.citrusmall.citrusstock.dto.ProductCreateRequest;
import com.citrusmall.citrusstock.dto.ProductResponse;
import com.citrusmall.citrusstock.model.Product;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface ProductMapper {
    ProductResponse toProductResponse(Product product);
    Product toProduct(ProductCreateRequest request);
}