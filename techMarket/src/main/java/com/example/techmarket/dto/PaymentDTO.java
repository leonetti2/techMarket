package com.example.techmarket.dto;

import com.example.techmarket.entities.PaymentMethod;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * @author lucio
 */

@Data
public class PaymentDTO {
    private int paymentId;
    private PaymentMethod paymentMethod;
    private BigDecimal amount;
    private LocalDateTime paidAt;
    private int orderId;
}
