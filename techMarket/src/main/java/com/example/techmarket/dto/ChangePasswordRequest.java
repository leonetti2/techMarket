package com.example.techmarket.dto;

import lombok.Data;

/**
 * @author lucio
 */

@Data
public class ChangePasswordRequest {
    private String oldPassword;
    private String newPassword;
}
