package com.example.techmarket.repositories;

import com.example.techmarket.entities.Product;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.math.BigDecimal;
import java.util.List;

/**
 * @author lucio
 */

public interface ProductRepository extends JpaRepository<Product, Integer>{
    List<Product> findByCategory(String category);

    List<Product> findByBrand(String brand);

    List<Product> findByNameContainingIgnoreCase(String name);

    @Query("""
SELECT p FROM Product p
WHERE (:name = '' OR LOWER(p.name) LIKE LOWER(CONCAT('%', :name, '%')))
AND (:category = '' OR LOWER(p.category) LIKE LOWER(CONCAT('%', :category, '%')))
AND (:brand = '' OR LOWER(p.brand) LIKE LOWER(CONCAT('%', :brand, '%')))
""")
    Page<Product> searchFilteredPaged(
            @Param("name") String name,
            @Param("category") String category,
            @Param("brand") String brand,
            Pageable pageable
    );

    @Query("""
    SELECT p FROM Product p
    WHERE (:name IS NULL OR :name = '' OR LOWER(p.name) LIKE LOWER(CONCAT('%', :name, '%')))
      AND (:category IS NULL OR :category = '' OR LOWER(p.category) LIKE LOWER(CONCAT('%', :category, '%')))
""")
    Page<Product> searchByNameAndCategory(@Param("name") String name, @Param("category") String category, Pageable pageable);
}
