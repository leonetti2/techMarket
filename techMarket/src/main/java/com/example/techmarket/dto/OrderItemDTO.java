package com.example.techmarket.dto;

import lombok.Data;

import java.math.BigDecimal;

/**
 * @author lucio
 */

@Data
public class OrderItemDTO {
    private int orderId;
    private int productId;
    private String productName;
    private int quantity;
    private BigDecimal price;
}
