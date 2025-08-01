package com.example.techmarket.repositories;

import com.example.techmarket.entities.Payment;
import com.example.techmarket.entities.Order;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

/**
 * @author lucio
 */

public interface PaymentRepository extends JpaRepository<Payment, Integer>{
    Optional<Payment> findByOrder(Order order);

    Optional<Payment> findById(int id);
}
