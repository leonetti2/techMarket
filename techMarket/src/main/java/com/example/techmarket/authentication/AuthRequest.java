package com.example.techmarket.authentication;

import lombok.Data;

/**
 * @author lucio
 */

@Data
public class AuthRequest {
    private String email;
    private String password;
    private String telephoneNumber;
    private String firstName;
    private String lastName;
    private String address;

}

