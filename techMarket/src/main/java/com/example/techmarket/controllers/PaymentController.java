package com.example.techmarket.controllers;

import com.example.techmarket.dto.PaymentDTO;
import com.example.techmarket.services.PaymentService;
import com.example.techmarket.support.ResponseMessage;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author lucio
 */

@RestController
@RequestMapping("/payments")
@RequiredArgsConstructor
public class PaymentController {
    private final PaymentService paymentService;

    @GetMapping("/{id}")
    public ResponseEntity<?> getPayment(@PathVariable int id){
        try {
            var payment = paymentService.getPaymentById(id);
            if (payment == null) {
                return ResponseEntity.status(404).body(new ResponseMessage("Payment not found"));
            }
            PaymentDTO dto = new PaymentDTO();
            dto.setPaymentId(payment.getId());
            dto.setPaymentMethod(payment.getMethod());
            dto.setAmount(payment.getAmount());
            dto.setPaidAt(payment.getPaidAt());
            dto.setOrderId(payment.getOrder().getId());
            return ResponseEntity.ok(dto);
        } catch (Exception e) {
            return ResponseEntity.status(500).body(new ResponseMessage("Error retrieving payment: " + e.getMessage()));
        }
    }

    @GetMapping("/order/{orderid}")
    public ResponseEntity<?> getPaymentByOrder(@PathVariable int orderid){
        try {
            var payment = paymentService.getPaymentByOrder(orderid);
            if (payment == null) {
                return ResponseEntity.status(404).body(new ResponseMessage("Payment for order not found"));
            }
            PaymentDTO dto = new PaymentDTO();
            dto.setPaymentId(payment.getId());
            dto.setPaymentMethod(payment.getMethod());
            dto.setAmount(payment.getAmount());
            dto.setPaidAt(payment.getPaidAt());
            dto.setOrderId(payment.getOrder().getId());
            return ResponseEntity.ok(dto);
        } catch (Exception e) {
            return ResponseEntity.status(500).body(new ResponseMessage("Error while retrieving payment for order: " + e.getMessage()));
        }
    }
}
