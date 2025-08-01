package com.example.techmarket.dto;

import com.example.techmarket.entities.Role;
import lombok.Data;

/**
 * @author lucio
 */

@Data
public class UserDTO {
    private int id;
    private String firstName;
    private String secondNamen;
    private String telephoneNumber;
    private String email;
    private String address;
    private Role role;
}
