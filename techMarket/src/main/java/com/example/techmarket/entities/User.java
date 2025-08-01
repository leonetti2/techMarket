package com.example.techmarket.entities;

import jakarta.persistence.*;
import lombok.*;

/**
 * @author lucio
 */

@Entity
@Data
@Table(name = "users")
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private int id;

    @Basic
    @Column(name = "password", nullable = false)
    private String password;

    @Basic
    @Column(name = "first_name", nullable = true, length = 50)
    private String firstName;

    @Basic
    @Column(name = "last_name", nullable = true, length = 50)
    private String lastName;

    @Basic
    @Column(name = "telephone_number", nullable = true, length = 20)
    private String telephoneNumber;

    @Basic
    @Column(name = "email", nullable = true, length = 90, unique = true)
    private String email;

    @Basic
    @Column(name = "address", nullable = true, length = 150)
    private String address;

    @Enumerated(EnumType.STRING)
    private Role role;
}
