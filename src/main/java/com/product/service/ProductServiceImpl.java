package com.product.service;

import com.product.dto.ProductDto;
import com.product.mapper.ModelMapperConfig;
import com.product.model.custom.CustomResponseEntity;
import com.product.model.entities.Product;
import com.product.repository.ProductRepository;
import org.modelmapper.ModelMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class ProductServiceImpl implements ProductService {

    private static final Logger logger = LoggerFactory.getLogger(ProductServiceImpl.class);

    private final ProductRepository productRepository;

    @Autowired
    ModelMapper getModelMapper;

    @Autowired
    ModelMapper modelMapper;

    @Autowired
    public ProductServiceImpl(ProductRepository productRepository) {
        this.productRepository = productRepository;
    }


    @Override
    public CustomResponseEntity<ProductDto> createNewProduct(ProductDto productDto) {
        try {
            logger.info("Attempting to create a new product: {}", productDto);

            Product product = modelMapper.map(productDto, Product.class);
            product = productRepository.save(product);

            logger.info("Product created successfully: {}", product);

            ProductDto createdProductDto = modelMapper.map(product, ProductDto.class);

            return new CustomResponseEntity<>(createdProductDto, "Product created successfully");

        } catch (Exception e) {
            logger.error("Error while creating product: {}", e.getMessage(), e);
            return CustomResponseEntity.error("Error while creating product");
        }
    }



    @Override
    public CustomResponseEntity<List<ProductDto>> fetchAllProducts() {
        try {
            logger.info("Fetching all products from the database");

            List<Product> products = productRepository.findAll();

            if (products.isEmpty()) {
                logger.warn("No products found in the database");
                return CustomResponseEntity.error("No products found");
            }

            List<ProductDto> productDtos = products.stream()
                    .map(product -> modelMapper.map(product, ProductDto.class))
                    .toList();



//            List<ProductDto> productDtos = products.stream()
//                    .map(ProductDto::new)
//                    .toList();

            logger.info("Successfully fetched {} products", products.size());
            return new CustomResponseEntity(productDtos,"Success");
        } catch (Exception e) {
            logger.error("Error while fetching all products: {}", e.getMessage(), e);
            return CustomResponseEntity.error("Error while fetching products");
        }
    }

    @Override
    public CustomResponseEntity<ProductDto> findProductByName(String name) {
        try {
            logger.info("Attempting to find product by name: {}", name);

            Optional<Product> productOpt = productRepository.findByName(name);

            if (productOpt.isEmpty()) {
                logger.warn("Product with name '{}' not found", name);
                return CustomResponseEntity.error("Product not found");
            }

            Product product = productOpt.get();
            logger.info("Product found: {}", product);
            return new CustomResponseEntity(new ProductDto(product),"Success");
        } catch (Exception e) {
            logger.error("Error while finding product by name: {}", e.getMessage(), e);
            return CustomResponseEntity.error("Error while finding product");
        }
    }

    @Override
    public CustomResponseEntity<?> deleteProduct(Long id) {
        try {
            logger.info("Attempting to delete product with ID: {}", id);

            if (!productRepository.existsById(id)) {
                logger.warn("Product with ID '{}' not found", id);
                return CustomResponseEntity.error("Product not found");
            }

            productRepository.deleteById(id);
            logger.info("Product with ID '{}' deleted successfully", id);
            return new CustomResponseEntity("Product deleted successfully");
        } catch (Exception e) {
            logger.error("Error while deleting product with ID '{}': {}", id, e.getMessage(), e);
            return CustomResponseEntity.error("Error while deleting product");
        }
    }

    @Override
    public CustomResponseEntity<ProductDto> updateProduct(ProductDto productDto) {
        try {
            logger.info("Attempting to update product: {}", productDto);

            Optional<Product> existingProductOpt = productRepository.findById(productDto.getId());

            if (existingProductOpt.isEmpty()) {
                logger.warn("Product with ID '{}' not found", productDto.getId());
                return CustomResponseEntity.error("Product not found");
            }

            Product existingProduct = existingProductOpt.get();
            existingProduct.setName(productDto.getName());
            existingProduct.setType(productDto.getType());
            existingProduct.setQuantity(productDto.getQuantity());

            Product updatedProduct = productRepository.save(existingProduct);

            logger.info("Product with ID '{}' updated successfully", updatedProduct.getId());
            return new CustomResponseEntity(new ProductDto(updatedProduct),"Success");
        } catch (Exception e) {
            logger.error("Error while updating product: {}", e.getMessage(), e);
            return CustomResponseEntity.error("Error while updating product");
        }
    }
}
