package com.example.techmarket.repositories;

import com.example.techmarket.entities.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

/**
 * @author lucio
 */

public interface UserRepository extends JpaRepository<User, Integer>{
    Optional<User> findByEmail(String email);

    boolean existsByEmail(String email);

    Optional<User> findById(int id);
    
}
