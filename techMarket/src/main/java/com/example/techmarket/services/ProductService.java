package com.example.techmarket.services;

import com.example.techmarket.entities.Product;
import com.example.techmarket.repositories.ProductRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

/**
 * @author lucio
 */

@Service
@RequiredArgsConstructor
public class ProductService {
    private final ProductRepository productRepository;

    @Transactional(readOnly = true)
    public List<Product> getAllProduct(){
        return productRepository.findAll();
    }

    @Transactional(readOnly = true)
    public Page<Product> getAllProduct(int pageNumber, int pageSize, String sortBy){
        Pageable paging = PageRequest.of(pageNumber, pageSize, Sort.by(sortBy));
        return productRepository.findAll(paging);
    }

    @Transactional(readOnly = true)
    public Product getProductById(int productId){
        return productRepository.findById(productId)
                .orElseThrow(() -> new RuntimeException("Product not found"));
    }

    @Transactional(readOnly = true)
    public Page<Product> getFilteredProductsPaged(String name, String category, String brand, String sortBy, String sortDir, int pageNumber, int pageSize) {
        Pageable pageable = PageRequest.of(pageNumber, pageSize, Sort.by(Sort.Direction.fromString(sortDir), sortBy));
        return productRepository.searchFilteredPaged(name, category, brand, pageable);
    }

    @Transactional(readOnly = false)
    public Product createProduct(Product product){
        return productRepository.save(product);
    }

    @Transactional(readOnly = false)
    public Product updateProduct(int id, Product updatedProduct){
        Product product = getProductById(id);
        product.setName(updatedProduct.getName());
        product.setBrand(updatedProduct.getBrand());
        product.setCategory(updatedProduct.getCategory());
        product.setDescription(updatedProduct.getDescription());
        product.setPrice(updatedProduct.getPrice());
        product.setQuantity(updatedProduct.getQuantity());
        product.setImageUrl(updatedProduct.getImageUrl());
        return productRepository.save(product);
    }

    @Transactional(readOnly = false)
    public Product updateStock(int id, int quantity){
        var product = productRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Product not found"));
        product.setQuantity(quantity);
        return productRepository.save(product);
    }

    @Transactional(readOnly = false)
    public void deleteProduct(int id){
        productRepository.deleteById(id);
    }

    @Transactional(readOnly = true)
    public long countProducts() {
        return productRepository.count();
    }

    @Transactional(readOnly = true)
    public Page<Product> searchProducts(String name, String category, Pageable pageable) {
        String nameParam = (name == null || name.isBlank()) ? null : name;
        String categoryParam = (category == null || category.isBlank()) ? null : category;
        return productRepository.searchByNameAndCategory(nameParam, categoryParam, pageable);
    }
}
