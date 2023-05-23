package com.crud.config.hateoas;

import com.crud.controller.ProductController;
import com.crud.dtos.response.ProductResponse;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;


import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.IanaLinkRelations;
import org.springframework.hateoas.server.RepresentationModelAssembler;
import org.springframework.stereotype.Component;

@Component
public class ProductHateoasConfig implements RepresentationModelAssembler<ProductResponse, EntityModel<ProductResponse>> {

    @Override
    public EntityModel<ProductResponse> toModel(ProductResponse entity) {
        EntityModel<ProductResponse> productModel = EntityModel.of(entity);

        productModel.add(linkTo(methodOn(ProductController.class).getProductById(entity.getId())).withSelfRel());
        productModel.add(linkTo(methodOn(ProductController.class).createProduct(null)).withRel("create"));
        productModel.add(linkTo(methodOn(ProductController.class).listProducts()).withRel(IanaLinkRelations.COLLECTION));

        return productModel;
    }

}
