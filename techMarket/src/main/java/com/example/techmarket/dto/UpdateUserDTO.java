package com.example.techmarket.dto;

import lombok.Data;

/**
 * @author lucio
 */

@Data
public class UpdateUserDTO {
    private String firstName;
    private String lastName;
    private String telephoneNumber;
    private String address;
}
