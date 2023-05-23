package com.crud.controller;

import com.crud.config.hateoas.ProductHateoasConfig;
import com.crud.dtos.request.ProductRequest;
import com.crud.dtos.response.ProductResponse;
import com.crud.dtos.response.RestResponse;
import com.crud.entities.CategoryEntity;
import com.crud.entities.ProductEntity;
import com.crud.services.ProductService;
import lombok.RequiredArgsConstructor;
import org.springframework.hateoas.CollectionModel;
import org.springframework.hateoas.EntityModel;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/products")
public class ProductController {

    private final ProductService productService;
    private final ProductHateoasConfig productHateoasConfig;

    @GetMapping
    public RestResponse<CollectionModel<EntityModel<ProductResponse>>> listProducts() {
        List<EntityModel<ProductResponse>> productModels = productService.listProducts().stream()
                .map(productHateoasConfig::toModel).toList();

        CollectionModel<EntityModel<ProductResponse>> collectionModel = CollectionModel.of(productModels)
                .add(linkTo(methodOn(ProductController.class).listProducts()).withSelfRel());

        return new RestResponse<>("SUCCESS",
                String.valueOf(HttpStatus.OK),
                "PRODUCT SUCCESSFULLY READED", collectionModel);
    }


    @GetMapping("/{id}")
    public RestResponse<EntityModel<ProductResponse>> getProductById(@PathVariable Long id) {
        return new RestResponse<>("SUCCESS",
                String.valueOf(HttpStatus.OK),
                "PRODUCT ID: " + id + " SUCCESSFULLY READED",
                productHateoasConfig.toModel(productService.getProductById(id)));
    }

    @PostMapping
    public RestResponse<ProductResponse> createProduct(@RequestBody ProductRequest productRequest) {
        return new RestResponse<>("SUCCESS",
                String.valueOf(HttpStatus.CREATED),
                "PRODUCT SUCCESSFULLY CREATED",
                productService.createProduct(productRequest));
    }

    @PutMapping("/{id}")
    public RestResponse<ProductResponse> updatedProduct(@PathVariable Long id, @RequestBody ProductRequest productRequest) {
        return new RestResponse<>("SUCCESS",
                String.valueOf(HttpStatus.OK),
                "PRODUCT ID: " + id + " SUCCESSFULLY UPDATED",
                productService.updateProduct(id, productRequest));
    }

    @DeleteMapping("/{id}")
    public RestResponse<String> deleteProduct(@PathVariable Long id) {
        productService.deleteProduct(id);
        return new RestResponse<>("SUCCESS",
                String.valueOf(HttpStatus.OK),
                "PRODUCT ID: " + id + " SUCCESSFULLY DELETED",
                "null"); // Data null.
    }

}
