package com.crud.services.impl;

import com.crud.dtos.request.CategoryRequest;
import com.crud.dtos.response.CategoryResponse;
import com.crud.entities.CategoryEntity;
import com.crud.exceptions.BusinessException;
import com.crud.mapper.CategoryMapper;
import com.crud.repositories.CategoryRepository;
import com.crud.services.CategoryService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class CategoryServiceImpl implements CategoryService {

    private final CategoryRepository categoryRepository;
    private final CategoryMapper categoryMapper;

    @Override
    public List<CategoryResponse> listCategories() {
        List<CategoryEntity> productoEntities = categoryRepository.findAll();
        return Optional.of(productoEntities)
                .filter(list -> !list.isEmpty())
                .map(categoryMapper::categoryListToCategoryDtoList)
                .orElseThrow(() -> new BusinessException("P-204", HttpStatus.NO_CONTENT, "Lista Vacia de Productos"));
    }

    @Override
    public CategoryResponse getCategoryById(Long id) {
        CategoryEntity category = categoryRepository.findById(id)
                .orElseThrow(() -> new BusinessException("P-400", HttpStatus.BAD_REQUEST, "El Id de la Categoria no existe " + id));
        return categoryMapper.toDto(category);

 }

    @Override
    public CategoryResponse createCategory(CategoryRequest categoryRequest) {
        CategoryEntity categoriaEntity = categoryMapper.toEntity(categoryRequest);
        CategoryEntity savedCategoryEntity = categoryRepository.save(categoriaEntity);
        return categoryMapper.toDto(savedCategoryEntity);
    }

    @Override
    public CategoryResponse updateCategory(Long id, CategoryRequest categoryRequest) {
        CategoryEntity categoriaEntity = categoryRepository.findById(id)
                .orElseThrow(() -> new BusinessException("P-400", HttpStatus.BAD_REQUEST, "El Id de la Categoria no existe " + id));
        categoryMapper.updateCategoryFromDto(categoryRequest, categoriaEntity);
        categoriaEntity = categoryRepository.save(categoriaEntity);
        return categoryMapper.toDto(categoriaEntity);
    }


    @Override
    public void deleteCategory(Long id) {
        if (!categoryRepository.existsById(id)) {
            throw new BusinessException("P-400", HttpStatus.BAD_REQUEST, "El Id de la categoria no existe " + id);
        }
        categoryRepository.deleteById(id);
    }
}
