package com.crud.services.impl;

import com.crud.dtos.request.ProductRequest;
import com.crud.dtos.response.CategoryResponse;
import com.crud.dtos.response.PageableResponse;
import com.crud.dtos.response.ProductResponse;
import com.crud.entities.CategoryEntity;
import com.crud.entities.ProductEntity;
import com.crud.exceptions.BusinessException;
import com.crud.mapper.ProductMapper;
import com.crud.reports.export.ResourceExport;
import com.crud.repositories.CategoryRepository;
import com.crud.repositories.ProductRepository;
import com.crud.services.ProductService;
import com.crud.util.AppConstants;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.io.File;
import java.util.*;

@Service
@RequiredArgsConstructor
public class ProductServiceImpl implements ProductService {

    private final ProductRepository productRepository;
    private final CategoryRepository categoryRepository;
    private final ProductMapper productMapper;
    private final ResourceExport resourceExport;

    @Override
    public List<ProductResponse> listProducts() {
        List<ProductEntity> productoEntities = productRepository.findAll();
        return Optional.of(productoEntities)
                .filter(list -> !list.isEmpty())
                .map(productMapper::productsToProductDtos)
                .orElseThrow(() -> new BusinessException("P-204", HttpStatus.NO_CONTENT, "Lista Vaciá de Productos"));
    }

    @Override
    public PageableResponse<ProductResponse> pageableProducts(int numeroDePagina, int medidaDePagina, String ordenarPor, String sortDir) {
        Sort sort = sortDir.equalsIgnoreCase(Sort.Direction.ASC.name()) ? Sort.by(ordenarPor).ascending()
                : Sort.by(ordenarPor).descending();
        Pageable pageable = PageRequest.of(numeroDePagina, medidaDePagina, sort);

        Page<ProductEntity> products = productRepository.findAll(pageable);

        List<ProductEntity> listProducts = products.getContent();
        List<ProductResponse> contenido = listProducts.stream().map(productMapper::toDto).toList();

        if (contenido.isEmpty()) {
            throw new BusinessException("P-204", HttpStatus.NO_CONTENT, "Lista Vaciá de Productos");
        }

        PageableResponse<ProductResponse> pageProductResponse = new PageableResponse<>();
        pageProductResponse.setContent(contenido);
        pageProductResponse.setPageNumber(products.getNumber());
        pageProductResponse.setPageSize(products.getSize());
        pageProductResponse.setTotalElements(products.getTotalElements());
        pageProductResponse.setTotalPages(products.getTotalPages());
        pageProductResponse.setLast(products.isLast());
        return pageProductResponse;
    }

    @Override
    public ProductResponse getProductById(Long id) {
        ProductEntity entity = productRepository.findById(id)
                .orElseThrow(() -> new BusinessException(AppConstants.BAD_REQUEST, HttpStatus.BAD_REQUEST, AppConstants.BAD_REQUEST_PRODUCT + id));
        return productMapper.toDto(entity);
    }

    @Override
    public ProductResponse createProduct(ProductRequest productRequest) {
        CategoryEntity categoriaEntity = categoryRepository.findById(productRequest.getCategoryId())
                .orElseThrow(() -> new BusinessException(AppConstants.BAD_REQUEST, HttpStatus.BAD_REQUEST, AppConstants.BAD_REQUEST_PRODUCT + productRequest.getCategoryId()));
        ProductEntity productoEntity = productMapper.toEntity(productRequest);
        productoEntity.setCategory(categoriaEntity);
        ProductEntity savedProductEntity = productRepository.save(productoEntity);
        return productMapper.toDto(savedProductEntity);
    }

    @Override
    public ProductResponse updateProduct(Long id, ProductRequest productRequest) {
        ProductEntity productEntity = productRepository.findById(id)
                .orElseThrow(() -> new BusinessException(AppConstants.BAD_REQUEST, HttpStatus.BAD_REQUEST, AppConstants.BAD_REQUEST_PRODUCT + id));

        // Verificar si la categoríaId es válida
        Long categoriaId = productRequest.getCategoryId();
        if (categoriaId != null) {
            CategoryEntity categoriaEntity = categoryRepository.findById(categoriaId)
                    .orElseThrow(() -> new BusinessException(AppConstants.BAD_REQUEST, HttpStatus.BAD_REQUEST, AppConstants.BAD_REQUEST_CATEGORY + categoriaId));
            productEntity.setCategory(categoriaEntity);
        }

        productMapper.updateProductFromDto(productRequest, productEntity);

        productEntity = productRepository.save(productEntity);
        return productMapper.toDto(productEntity);
    }

    @Override
    public void deleteProduct(Long id) {
        if (!productRepository.existsById(id)) {
            throw new BusinessException(AppConstants.BAD_REQUEST, HttpStatus.BAD_REQUEST, AppConstants.BAD_REQUEST_PRODUCT + id);
        }
        productRepository.deleteById(id);
    }

    //Implementation Patron de diseño(SOLID)
/*   Principio de responsabilidad única (SRP): El método exportDataExcel se encarga únicamente de orquestar la exportación de los datos,
     mientras que la lógica relacionada con la validación de formato,
     creación de columnas y valores, se ha separado en métodos auxiliares con responsabilidades más específicas.
    */
    @Override
    public File exportDataExcel(List<ProductResponse> productResponses, String formato) throws Exception {
        validateFormato(formato);

        List<String> sheets = Collections.singletonList(AppConstants.SHEET_PRODUCT);

        Map<String, List<String>> colsBySheet = createColumnsBySheetMap();
        Map<String, List<Map<String, String>>> valuesBySheet = createValuesBySheetMap(productResponses);

        String reportName = AppConstants.REPORT_NAME_PRODUCT_PAGINABLE;
        if (formato.equals(AppConstants.FORMATO_EXCEL_ABREVIATURA)) {
            return resourceExport.generateExcel(sheets, colsBySheet, valuesBySheet, reportName);
        } else {
            return resourceExport.generatePdf(sheets, colsBySheet, valuesBySheet, reportName);
        }
    }

    //Validated Format
    private void validateFormato(String formato) {
        if (!AppConstants.ARRAY_FORMATO.contains(formato)) {
            throw new BusinessException(String.format("%s format not allowed", formato), HttpStatus.BAD_GATEWAY, "Bad");
        }
    }

    //Mapeando Columnas
    private Map<String, List<String>> createColumnsBySheetMap() {
        List<String> cols = Arrays.asList(
                AppConstants.COL_PRODUCT_ID,
                AppConstants.COL_PRODUCT_NAME,
                AppConstants.COL_PRODUCT_PRICE,
                AppConstants.COL_PRODUCT_DESCRIPTION,
                AppConstants.COL_CATEGORY_ID,
                AppConstants.COL_CATEGORY_NAME
        );

        Map<String, List<String>> colsBySheet = new HashMap<>();
        colsBySheet.put(AppConstants.SHEET_PRODUCT, cols);
        return colsBySheet;
    }

    private Map<String, List<Map<String, String>>> createValuesBySheetMap(List<ProductResponse> productResponses) {
        List<Map<String, String>> valoresHoja = new ArrayList<>();

        for (ProductResponse row : productResponses) {
            Map<String, String> valuesHojaRow = new HashMap<>();
            valuesHojaRow.put(AppConstants.COL_PRODUCT_ID, getStringValue(row.getId()));
            valuesHojaRow.put(AppConstants.COL_PRODUCT_NAME, getStringValue(row.getName()));
            valuesHojaRow.put(AppConstants.COL_PRODUCT_PRICE, getStringValue(row.getPrice()));
            valuesHojaRow.put(AppConstants.COL_PRODUCT_DESCRIPTION, getStringValue(row.getDescription()));
            CategoryResponse category = row.getCategory();
            valuesHojaRow.put(AppConstants.COL_CATEGORY_ID, category != null ? getStringValue(category.getId()) : "");
            valuesHojaRow.put(AppConstants.COL_CATEGORY_NAME, category != null ? getStringValue(category.getName()) : "");
            valoresHoja.add(valuesHojaRow);
        }

        Map<String, List<Map<String, String>>> valuesBySheet = new HashMap<>();
        valuesBySheet.put(AppConstants.SHEET_PRODUCT, valoresHoja);
        return valuesBySheet;
    }

    private String getStringValue(Object value) {
        return value != null ? value.toString() : AppConstants.VC_EMTY;
    }


}
