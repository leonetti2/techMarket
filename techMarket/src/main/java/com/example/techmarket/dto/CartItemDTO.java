package com.example.techmarket.dto;

import lombok.AllArgsConstructor;
import lombok.Data;


import java.math.BigDecimal;

/**
 * @author lucio
 */

@Data
@AllArgsConstructor
public class CartItemDTO {
    private int id;
    private int productId;
    private String productName;
    private int quantity;
    private BigDecimal price;

}
