package com.crud.services;

import com.crud.dtos.request.ProductRequest;
import com.crud.dtos.response.ProductResponse;

import java.util.List;
import java.util.Optional;

public interface ProductService {

    List<ProductResponse> listProducts();
    ProductResponse getProductById(Long id);
    ProductResponse createProduct(ProductRequest productRequest);
    ProductResponse updateProduct(Long id, ProductRequest productRequest);
    void deleteProduct(Long id);
}
