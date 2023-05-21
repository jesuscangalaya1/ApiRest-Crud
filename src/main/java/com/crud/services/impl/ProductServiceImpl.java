package com.crud.services.impl;

import com.crud.dtos.request.ProductRequest;
import com.crud.dtos.response.ProductResponse;
import com.crud.entities.CategoryEntity;
import com.crud.entities.ProductEntity;
import com.crud.exceptions.BusinessException;
import com.crud.mapper.ProductMapper;
import com.crud.repositories.CategoryRepository;
import com.crud.repositories.ProductRepository;
import com.crud.services.ProductService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class ProductServiceImpl implements ProductService {

    private final ProductRepository productRepository;
    private final CategoryRepository categoryRepository;
    private final ProductMapper productMapper;

    @Override
    public List<ProductResponse> listProducts() {
        List<ProductEntity> productoEntities = productRepository.findAll();
        return Optional.of(productoEntities)
                .filter(list -> !list.isEmpty())
                .map(productMapper::productsToProductDtos)
                .orElseThrow(() -> new BusinessException("P-204", HttpStatus.NO_CONTENT, "Lista Vacia de Productos"));
    }

    @Override
    public Optional<ProductResponse> getProductById(Long id) {
        ProductEntity entity = productRepository.findById(id)
                .orElseThrow(() -> new BusinessException("P-400", HttpStatus.BAD_REQUEST, "El Id del Producto no existe " + id));
        return Optional.of(productMapper.toDto(entity));
    }

    @Override
    public ProductResponse createProduct(ProductRequest productRequest) {
        CategoryEntity categoriaEntity = categoryRepository.findById(productRequest.getCategoryId())
                .orElseThrow(() -> new BusinessException("P-400", HttpStatus.BAD_REQUEST, "El Id del Producto no existe " + productRequest.getCategoryId()));
        ProductEntity productoEntity = productMapper.toEntity(productRequest);
        productoEntity.setCategory(categoriaEntity);
        ProductEntity savedProductEntity = productRepository.save(productoEntity);
        return productMapper.toDto(savedProductEntity);
    }

    @Override
    public ProductResponse updateProduct(Long id, ProductRequest productRequest) {
        ProductEntity productEntity = productRepository.findById(id)
                .orElseThrow(() -> new BusinessException("P-400", HttpStatus.BAD_REQUEST, "El Id del Producto no existe " + id));

        // Verificar si la categoríaId es válida
        Long categoriaId = productRequest.getCategoryId();
        if (categoriaId != null) {
            CategoryEntity categoriaEntity = categoryRepository.findById(categoriaId)
                    .orElseThrow(() -> new BusinessException("P-400", HttpStatus.BAD_REQUEST, "El Id de la Categoría no existe " + categoriaId));
            productEntity.setCategory(categoriaEntity);
        }

        productMapper.updateProductFromDto(productRequest, productEntity);

        productEntity = productRepository.save(productEntity);
        return productMapper.toDto(productEntity);
    }

    @Override
    public void deleteProduct(Long id) {
        if (!productRepository.existsById(id)) {
            throw new BusinessException("P-400", HttpStatus.BAD_REQUEST, "El Id del Producto no existe " + id);
        }
        productRepository.deleteById(id);
    }
}
