package com.example.techmarket.dto;

import lombok.Data;
import java.math.BigDecimal;

/**
 * @author lucio
 */

@Data
public class ProductDTO {
    private int id;
    private String name;
    private String brand;
    private String cateogory;
    private String description;
    private BigDecimal price;
    private int quantity;
    private String imageUrl;
}
