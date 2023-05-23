package com.crud.controller;

import com.crud.config.hateoas.CategoryHateoasConfig;
import com.crud.dtos.request.CategoryRequest;
import com.crud.dtos.response.CategoryResponse;
import com.crud.dtos.response.ProductResponse;
import com.crud.dtos.response.RestResponse;
import com.crud.services.CategoryService;
import lombok.RequiredArgsConstructor;
import org.springframework.hateoas.EntityModel;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.hateoas.CollectionModel;


import java.util.List;
import java.util.Optional;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/categories")
public class CategoryController {

    private final CategoryService categoryService;
    private final CategoryHateoasConfig categoryHateoasConfig;

    @GetMapping
    public RestResponse<CollectionModel<EntityModel<CategoryResponse>>> listCategories() {
        List<EntityModel<CategoryResponse>> categoryModels = categoryService.listCategories().stream()
                .map(categoryHateoasConfig::toModel).toList();

        CollectionModel<EntityModel<CategoryResponse>> collectionModel = CollectionModel.of(categoryModels)
                .add(linkTo(methodOn(CategoryController.class).listCategories()).withSelfRel());

        return new RestResponse<>("SUCCESS",
                String.valueOf(HttpStatus.OK),
                "CATEGORIES SUCCESSFULLY READED", collectionModel);
    }

    @GetMapping("/{id}")
    public RestResponse<EntityModel<CategoryResponse>> getCategoryById(@PathVariable Long id) {
        return new RestResponse<>("SUCCESS",
                String.valueOf(HttpStatus.OK),
                "BRANDCAR ID: " + id + " SUCCESSFULLY READED",
                categoryHateoasConfig.toModel(categoryService.getCategoryById(id)));
    }

    @PostMapping
    public RestResponse<CategoryResponse> createCategory(@RequestBody CategoryRequest categoryRequest) {
        return new RestResponse<>("SUCCESS",
                String.valueOf(HttpStatus.CREATED),
                "CATEGORY SUCCESSFULLY CREATED",
                categoryService.createCategory(categoryRequest));
    }

    @PutMapping("/{id}")
    public RestResponse<CategoryResponse> updatedCategory(@PathVariable Long id, @RequestBody CategoryRequest categoryRequest) {
        return new RestResponse<>("SUCCESS",
                String.valueOf(HttpStatus.OK),
                "CATEGORY ID: " + id + " SUCCESSFULLY UPDATED",
                categoryService.updateCategory(id, categoryRequest));
    }

    @DeleteMapping("/{id}")
    public RestResponse<String> deleteCategory(@PathVariable Long id) {
        categoryService.deleteCategory(id);
        return new RestResponse<>("SUCCESS",
                String.valueOf(HttpStatus.OK),
                "CATEGORY ID: " + id + " SUCCESSFULLY DELETED",
                "null"); // Data null.
    }

}
