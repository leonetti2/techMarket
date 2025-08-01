package com.example.techmarket.repositories;

import com.example.techmarket.entities.Cart;
import com.example.techmarket.entities.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.Optional;
/**
 * @author lucio
 */

public interface CartRepository extends JpaRepository<Cart, Integer> {
    Optional<Cart> findByUser(User user);

    boolean existsByUser(User user);
}
