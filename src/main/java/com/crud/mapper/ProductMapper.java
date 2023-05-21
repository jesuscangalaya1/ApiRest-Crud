package com.crud.mapper;

import com.crud.dtos.request.ProductRequest;
import com.crud.dtos.response.ProductResponse;
import com.crud.entities.ProductEntity;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

import java.util.List;

@Mapper
public interface ProductMapper {

    @Mapping(source = "category.id", target = "category.id")
    ProductResponse toDto(ProductEntity productEntity);

    @Mapping(source = "categoryId", target = "category.id")
    ProductEntity toEntity(ProductRequest productRequest);

    // UpdateProduct
    @Mapping(target = "id", ignore = true)
    void updateProductFromDto(ProductRequest productRequest, @MappingTarget ProductEntity productEntity);


    // ListWithCategories
    List<ProductResponse> productsToProductDtos(List<ProductEntity> productEntityList);
}

