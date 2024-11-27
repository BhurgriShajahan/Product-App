package com.product.controller;

import com.product.dto.ProductDto;
import com.product.model.custom.CustomResponseEntity;
import com.product.service.ProductService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/v1/product")
public class ProductController {

    @Autowired
    ProductService productService;

    //Create Product
    @PostMapping("/create")
    public CustomResponseEntity<?> createNewProduct(@RequestBody ProductDto productDto)
    {
         return productService.createNewProduct(productDto);
    }

    //Fetch All Products
    @GetMapping("/fetchAll")
    public CustomResponseEntity fetchAllProducts()
    {
        return productService.fetchAllProducts();
    }


}
