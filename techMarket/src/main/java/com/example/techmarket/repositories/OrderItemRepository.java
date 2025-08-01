package com.example.techmarket.repositories;

import com.example.techmarket.entities.OrderItem;
import com.example.techmarket.entities.Order;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

/**
 * @author lucio
 */

public interface OrderItemRepository extends JpaRepository<OrderItem, Integer>{
    List<OrderItem> findByOrder(Order order);
}
