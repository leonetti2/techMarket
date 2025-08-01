package com.example.techmarket.controllers;

import java.math.BigDecimal;
import java.util.List;
import java.util.stream.Collectors;

import com.example.techmarket.dto.ProductDTO;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.example.techmarket.entities.Product;
import com.example.techmarket.services.ProductService;
import com.example.techmarket.support.ResponseMessage;

import lombok.RequiredArgsConstructor;

/**
 * @author lucio
 */

@RestController
@RequestMapping("/products")
@RequiredArgsConstructor
public class ProductController {
    private final ProductService productService;

    @GetMapping
    public ResponseEntity<?> getAllProducts(){
        try {
            List<ProductDTO> dtos = productService.getAllProduct().stream().map(this::toDTO).collect(Collectors.toList());
            return ResponseEntity.ok(dtos);
        } catch (Exception e) {
            return ResponseEntity.status(500).body(new ResponseMessage("Error retrieving products: " + e.getMessage()));
        }
    }

    @GetMapping("/paged")
    public ResponseEntity<?> getAllProducts(
            @RequestParam(value = "pageNumber", defaultValue = "0") int pageNumber,
            @RequestParam(value = "pageSize", defaultValue = "9") int pageSize,
            @RequestParam(value = "sortBy", defaultValue = "id") String sortBy){
        try {
            Page<Product> result = productService.getAllProduct(pageNumber, pageSize, sortBy);
            Page<ProductDTO> dtoPage = result.map(this::toDTO);
            return ResponseEntity.ok(dtoPage);
        } catch (Exception e) {
            return ResponseEntity.status(500).body(new ResponseMessage("Error retrieving paginated products: " + e.getMessage()));
        }
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getProduct(@PathVariable int id){
        try {
            Product product = productService.getProductById(id);
            if (product == null) {
                return ResponseEntity.status(404).body(new ResponseMessage("Product not found"));
            }
            var productDTO = toDTO(product);
            return ResponseEntity.ok(productDTO);
        } catch (Exception e) {
            return ResponseEntity.status(500).body(new ResponseMessage("Error retrieving product: " + e.getMessage()));
        }
    }

    @GetMapping("/filtered")
    public ResponseEntity<?> getFilteredProducts(
            @RequestParam(required = false) String name,
            @RequestParam(required = false) String category,
            @RequestParam(required = false) String brand,
            @RequestParam(defaultValue = "id") String sortBy,
            @RequestParam(defaultValue = "asc") String sortDir,
            @RequestParam(defaultValue = "0") int pageNumber,
            @RequestParam(defaultValue = "9") int pageSize) {
        try {
            name = (name == null) ? "" : name;
            category = (category == null) ? "" : category;
            brand = (brand == null) ? "" : brand;

            Page<Product> filtered = productService.getFilteredProductsPaged(name, category, brand, sortBy, sortDir, pageNumber, pageSize);
            Page<ProductDTO> dtoPage = filtered.map(this::toDTO);

            return ResponseEntity.ok(dtoPage);
        } catch (Exception e) {
            return ResponseEntity.status(500).body(new ResponseMessage("Error filtering products: " + e.getMessage()));
        }
    }

    private ProductDTO toDTO(Product p) {
        ProductDTO dto = new ProductDTO();
        dto.setId(p.getId());
        dto.setName(p.getName());
        dto.setBrand(p.getBrand());
        dto.setCateogory(p.getCategory());
        dto.setDescription(p.getDescription());
        dto.setPrice(p.getPrice());
        dto.setQuantity(p.getQuantity());
        dto.setImageUrl(p.getImageUrl());
        return dto;
    }
}
