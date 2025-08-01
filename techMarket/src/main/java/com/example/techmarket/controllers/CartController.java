package com.example.techmarket.controllers;

import com.example.techmarket.dto.CartDTO;
import com.example.techmarket.dto.CartItemDTO;
import com.example.techmarket.entities.Cart;
import com.example.techmarket.services.CartService;
import com.example.techmarket.support.ResponseMessage;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * @author lucio
 */

@RestController
@RequestMapping("/cart")
@RequiredArgsConstructor
public class CartController {
    private final CartService cartService;


    @GetMapping("/{userId}")
    public ResponseEntity<?> getCartByUser(@PathVariable int userId){
        try {
            var cart = cartService.getCartByUserId(userId);
            if (cart == null)
                return ResponseEntity.status(404).body(new ResponseMessage("Cart not found"));
            List<CartItemDTO> items = cart.getItems().stream().map(item -> new CartItemDTO(
                    item.getId(),
                    item.getProduct().getId(),
                    item.getProduct().getName(),
                    item.getQuantity(),
                    item.getProduct().getPrice()
            )).toList();
            CartDTO dto = new CartDTO(cart.getId(), items);
            return ResponseEntity.ok(dto);
        } catch (Exception e) {
            return ResponseEntity.status(500).body(new ResponseMessage("Error retrieving cart: " + e.getMessage()));
        }
    }

    @PreAuthorize("hasRole('USER')")
    @PostMapping("/{userId}/add/{productId}")
    public ResponseEntity<?> addProductToCart(@PathVariable int userId, @PathVariable int productId, @RequestParam int quantity){
        try {
            var cart = cartService.addProductToCart(userId, productId, quantity);
            cart.getItems().removeIf(item -> item.getQuantity() <= 0);
            return new ResponseEntity<>(cart, HttpStatus.OK);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(new ResponseMessage("Error adding to cart: " + e.getMessage()));
        }
    }

    @DeleteMapping("/{userId}/remove/{productId}")
    public ResponseEntity<?> removeProductFromCart(@PathVariable int userId, @PathVariable int productId){
        try {
            var cart = cartService.removeItem(userId, productId);
            return ResponseEntity.ok(cart);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(new ResponseMessage("Error while removing from cart: " + e.getMessage()));
        }
    }

    @PostMapping("/{userid}/clear")
    public ResponseEntity<?> clearCart(@PathVariable int userid){
        try {
            cartService.clearCart(userid);
            return ResponseEntity.noContent().build();
        } catch (Exception e) {
            return ResponseEntity.status(500).body(new ResponseMessage("Error while cleaning the cart: " + e.getMessage()));
        }
    }
}
