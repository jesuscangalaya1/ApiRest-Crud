package com.crud.controller;

import com.crud.dtos.request.ProductRequest;
import com.crud.dtos.response.ProductResponse;
import com.crud.dtos.response.RestResponse;
import com.crud.services.ProductService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/products")
public class ProductController {

    private final ProductService productService;

    @GetMapping
    public RestResponse<List<ProductResponse>> listProducts() {
        return new RestResponse<>("SUCCESS",
                String.valueOf(HttpStatus.OK),
                "PRODUCT SUCCESSFULLY READED",
                productService.listProducts());
    }

    @GetMapping("/{id}")
    public RestResponse<Optional<ProductResponse>> getProductById(@PathVariable Long id) {
        return new RestResponse<>("SUCCESS",
                String.valueOf(HttpStatus.OK),
                "PRODUCT ID: " + id + " SUCCESSFULLY READED",
                productService.getProductById(id));
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
