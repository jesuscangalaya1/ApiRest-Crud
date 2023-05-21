package com.crud.controller;

import com.crud.dtos.request.CategoryRequest;
import com.crud.dtos.response.CategoryResponse;
import com.crud.dtos.response.RestResponse;
import com.crud.services.CategoryService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/categories")
public class CategoryController {

    private final CategoryService categoryService;

    @GetMapping
    public RestResponse<List<CategoryResponse>> listCategories() {
        return new RestResponse<>("SUCCESS",
                String.valueOf(HttpStatus.OK),
                "CATEGORIES SUCCESSFULLY READED",
                categoryService.listCategories());
    }

    @GetMapping("/{id}")
    public RestResponse<Optional<CategoryResponse>> getCategoryById(@PathVariable Long id) {
        return new RestResponse<>("SUCCESS",
                String.valueOf(HttpStatus.OK),
                "BRANDCAR ID: " + id + " SUCCESSFULLY READED",
                categoryService.getCategoryById(id));
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
