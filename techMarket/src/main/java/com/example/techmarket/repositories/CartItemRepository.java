package com.example.techmarket.repositories;

import com.example.techmarket.entities.CartItem;
import com.example.techmarket.entities.Cart;
import com.example.techmarket.entities.Product;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

/**
 * @author lucio
 */

public interface CartItemRepository extends JpaRepository<CartItem, Integer>{
    List<CartItem> findByCart(Cart cart);

    Optional<CartItem> findByCartAndProduct(Cart cart, Product product);

    void deleteByCart(Cart cart);
}
