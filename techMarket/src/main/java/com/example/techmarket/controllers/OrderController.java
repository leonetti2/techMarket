package com.example.techmarket.controllers;

import java.util.List;

import com.example.techmarket.dto.UserDTO;
import com.example.techmarket.repositories.PaymentRepository;
import com.example.techmarket.support.ResponseMessage;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.example.techmarket.dto.CheckoutRequestDTO;
import com.example.techmarket.dto.OrderDTO;
import com.example.techmarket.dto.OrderItemDTO;
import com.example.techmarket.entities.Order;
import com.example.techmarket.services.CheckoutService;
import com.example.techmarket.services.OrderService;

import lombok.RequiredArgsConstructor;

/**
 * @author lucio
 */

@RestController
@RequestMapping("/orders")
@RequiredArgsConstructor
public class OrderController {
    private final OrderService orderService;
    private final CheckoutService checkoutService;
    private final PaymentRepository paymentRepository;

    @PostMapping("/checkout")
    public ResponseEntity<?> checkout(@RequestBody CheckoutRequestDTO request){
        try {
            var order = checkoutService.checkout(request);

            OrderDTO dto = new OrderDTO();
            dto.setOrderId(order.getId());
            dto.setUserId(order.getUser().getId());
            if (order.getUser() != null) {
                UserDTO userDTO = new UserDTO();
                userDTO.setId(order.getUser().getId());
                userDTO.setFirstName(order.getUser().getFirstName());
                userDTO.setSecondNamen(order.getUser().getLastName());
                userDTO.setEmail(order.getUser().getEmail());
                userDTO.setTelephoneNumber(order.getUser().getTelephoneNumber());
                userDTO.setAddress(order.getUser().getAddress());
                userDTO.setRole(order.getUser().getRole());
                dto.setUser(userDTO);
            }
            if (order.getItems() != null) {
                dto.setItems(order.getItems().stream().map(item -> {
                    OrderItemDTO itemDto = new OrderItemDTO();
                    itemDto.setOrderId(order.getId());
                    itemDto.setProductId(item.getProduct().getId());
                    itemDto.setProductName(item.getProduct().getName());
                    itemDto.setQuantity(item.getQuantity());
                    itemDto.setPrice(item.getPrice());
                    return itemDto;
                }).toList());
            }
            dto.setTotalPrice(order.getTotalPrice());
            dto.setStatus(order.getStatus());
            dto.setCreatedAt(order.getCreatedAt());

            paymentRepository.findByOrder(order).ifPresent(payment -> {
                dto.setPaymentMethod(payment.getMethod());
            });

            return ResponseEntity.ok(dto);
        } catch (Exception e) {
            return ResponseEntity.status(500).body(new ResponseMessage("Error during checkout: " + e.getMessage()));
        }
    }

    @GetMapping("/user/{userId}")
    public ResponseEntity<?> getOrdersByUser(@PathVariable int userId){
        try {
            List<Order> orders = orderService.getOrdersByUser(userId);
            List<OrderDTO> dtos = orders.stream().map(order -> {
                OrderDTO dto = new OrderDTO();
                dto.setOrderId(order.getId());
                dto.setUserId(order.getUser().getId());
                dto.setItems(order.getItems().stream().map(item -> {
                    OrderItemDTO itemDto = new OrderItemDTO();
                    itemDto.setOrderId(order.getId());
                    itemDto.setProductId(item.getProduct().getId());
                    itemDto.setProductName(item.getProduct().getName());
                    itemDto.setQuantity(item.getQuantity());
                    itemDto.setPrice(item.getPrice());
                    return itemDto;
                }).toList());
                dto.setTotalPrice(order.getTotalPrice());
                dto.setStatus(order.getStatus());
                dto.setCreatedAt(order.getCreatedAt());
                return dto;
            }).toList();
            return ResponseEntity.ok(dtos);
        } catch (Exception e) {
            return ResponseEntity.status(500).body(new ResponseMessage("Error retrieving orders: " + e.getMessage()));
        }
    }

    @GetMapping("/{orderId}")
    public ResponseEntity<?> getOrderById(@PathVariable int orderId) {
        try {
            var order = orderService.getOrderById(orderId);
            if (order == null) {
                return ResponseEntity.status(404).body(new ResponseMessage("Ordine non trovato"));
            }
            OrderDTO dto = new OrderDTO();
            dto.setOrderId(order.getId());
            dto.setUserId(order.getUser().getId());
            dto.setItems(order.getItems().stream().map(item -> {
                OrderItemDTO itemDto = new OrderItemDTO();
                itemDto.setOrderId(order.getId());
                itemDto.setProductId(item.getProduct().getId());
                itemDto.setProductName(item.getProduct().getName());
                itemDto.setQuantity(item.getQuantity());
                itemDto.setPrice(item.getPrice());
                return itemDto;
            }).toList());
            dto.setTotalPrice(order.getTotalPrice());
            dto.setStatus(order.getStatus());
            dto.setCreatedAt(order.getCreatedAt());
            paymentRepository.findByOrder(order).ifPresent(payment -> {
                dto.setPaymentMethod(payment.getMethod());
            });
            return ResponseEntity.ok(dto);
        } catch (Exception e) {
            return ResponseEntity.status(500).body(new ResponseMessage("Error retrieving order: " + e.getMessage()));
        }
    }

    @PostMapping("/{orderId}/return")
    public ResponseEntity<?> responseReturn(@PathVariable int orderId, Authentication authentication){
        String userEmail = authentication.getName();
        try {
            orderService.requestReturn(orderId, userEmail);
            return ResponseEntity.ok(new ResponseMessage("Return request sent successfully"));
        } catch (RuntimeException e){
            return ResponseEntity.badRequest().body(new ResponseMessage("Error while requesting a return: " + e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.status(500).body(new ResponseMessage("Internal error: " + e.getMessage()));
        }
    }

}
