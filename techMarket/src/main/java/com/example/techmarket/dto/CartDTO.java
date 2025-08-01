package com.example.techmarket.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.List;

/**
 * @author lucio
 */

@Data
@AllArgsConstructor
public class CartDTO {
    private int cartId;
    private List<CartItemDTO> items;
}
