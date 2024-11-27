package com.product.service;

import com.product.dto.ProductDto;
import com.product.model.custom.CustomResponseEntity;

import java.util.List;

public interface ProductService {

    //Create Product
    CustomResponseEntity<ProductDto> createNewProduct(ProductDto productDto);
    //Get All Products
    CustomResponseEntity<List<ProductDto>> fetchAllProducts();
    //Find Product By name
    CustomResponseEntity<ProductDto> findProductByName(String name);
    //Delete Product
    CustomResponseEntity<?> deleteProduct(Long id);
    //Update Product
    CustomResponseEntity<ProductDto> updateProduct(ProductDto productDto);

}
