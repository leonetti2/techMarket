package com.example.techmarket.services;

import com.example.techmarket.dto.CheckoutRequestDTO;
import com.example.techmarket.entities.*;
import com.example.techmarket.repositories.*;
import org.springframework.transaction.annotation.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * @author lucio
 */

@Service
@RequiredArgsConstructor
public class CheckoutService {
    private final CartService cartService;
    private final OrderRepository orderRepository;
    private final OrderItemRepository orderItemRepository;
    private final PaymentRepository paymentRepository;
    private final ProductRepository productRepository;
    private final UserRepository userRepository;

    @Transactional(readOnly = false)
    public Order checkout(CheckoutRequestDTO request){
        var user = userRepository.findById(request.getUserId())
                .orElseThrow(() -> new RuntimeException("User not found"));

        var cartItems = cartService.getCartItems(user);
        var paymentMethod = request.getPaymentMethod();

        if(cartItems.isEmpty())
            throw new RuntimeException("Cart is empty");

        Order order = new Order();
        order.setUser(user);
        order.setCreatedAt(LocalDateTime.now());

        List<OrderItem> orderItems = new ArrayList<>();
        BigDecimal total = BigDecimal.ZERO;

        for(CartItem cartItem: cartItems){
            Product product = productRepository.findById(cartItem.getProduct().getId())
                    .orElseThrow(() -> new RuntimeException("Product not found"));

            if(product.getQuantity() < cartItem.getQuantity())
                throw new RuntimeException("Not enough stock for " + product.getName());

            product.setQuantity(product.getQuantity() - cartItem.getQuantity());
            productRepository.save(product);

            OrderItem orderItem = new OrderItem();
            orderItem.setOrder(order);
            orderItem.setProduct(product);
            orderItem.setQuantity(cartItem.getQuantity());
            orderItem.setPrice(product.getPrice());
            orderItems.add(orderItem);

            total = total.add(product.getPrice().multiply(BigDecimal.valueOf(cartItem.getQuantity())));
        }

        order.setItems(orderItems);
        order.setTotalPrice(total);
        order.setStatus(OrderStatus.COMPLETED);

        order = orderRepository.save(order);

        Payment payment = new Payment();
        payment.setOrder(order);
        payment.setAmount(total);
        payment.setMethod(paymentMethod);
        payment.setPaidAt(LocalDateTime.now());

        paymentRepository.save(payment);
        cartService.clearCart(user.getId());

        return order;
    }
}
