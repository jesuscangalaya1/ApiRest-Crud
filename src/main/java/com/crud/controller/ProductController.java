package com.crud.controller;

import com.crud.config.hateoas.ProductHateoasConfig;
import com.crud.dtos.request.ProductRequest;
import com.crud.dtos.response.PageableResponse;
import com.crud.dtos.response.ProductResponse;
import com.crud.dtos.response.RestResponse;
import com.crud.services.ProductService;
import com.crud.util.AppConstants;
import lombok.RequiredArgsConstructor;
import org.springframework.hateoas.EntityModel;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/products")
public class ProductController {

    private final ProductService productService;
    private final ProductHateoasConfig productHateoasConfig;


    @GetMapping
    public  RestResponse<PageableResponse<ProductResponse>> pageableProducts(
            @RequestParam(value = "pageNo", defaultValue = AppConstants.NUMERO_DE_PAGINA_POR_DEFECTO, required = false) int numeroDePagina,
            @RequestParam(value = "pageSize", defaultValue = AppConstants.MEDIDA_DE_PAGINA_POR_DEFECTO, required = false) int medidaDePagina,
            @RequestParam(value = "sortBy", defaultValue = AppConstants.ORDENAR_POR_DEFECTO, required = false) String ordenarPor,
            @RequestParam(value = "sortDir", defaultValue = AppConstants.ORDENAR_DIRECCION_POR_DEFECTO, required = false) String sortDir) {

        return new  RestResponse<>("SUCCESS",
                String.valueOf(HttpStatus.OK),
                "PRODUCT SUCCESSFULLY READED",
                productService.pageableProducts(numeroDePagina, medidaDePagina, ordenarPor, sortDir));

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
