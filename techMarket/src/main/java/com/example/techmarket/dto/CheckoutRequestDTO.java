package com.example.techmarket.dto;

import com.example.techmarket.entities.PaymentMethod;
import lombok.AllArgsConstructor;
import lombok.Data;

/**
 * @author lucio
 */

@Data
@AllArgsConstructor
public class CheckoutRequestDTO {
    private int userId;
    private PaymentMethod paymentMethod;
    private String cardNumber;
    private String cardHolder;
    private String cardExpiry;
    private String paypalEmail;
}
