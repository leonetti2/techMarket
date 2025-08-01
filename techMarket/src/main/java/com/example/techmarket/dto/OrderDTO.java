package com.example.techmarket.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

import com.example.techmarket.entities.Order;
import com.example.techmarket.entities.OrderStatus;

import com.example.techmarket.entities.PaymentMethod;
import lombok.Data;

/**
 * @author lucio
 */

@Data
public class OrderDTO {
    private int orderId;
    private int userId;
    private UserDTO user;
    private List<OrderItemDTO> items;
    private BigDecimal totalPrice;
    private OrderStatus status;
    private LocalDateTime createdAt;
    private PaymentMethod paymentMethod;

}
