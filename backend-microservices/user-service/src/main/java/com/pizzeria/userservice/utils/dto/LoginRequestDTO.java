package com.pizzeria.userservice.utils.dto;

import lombok.Data;

@Data
public class LoginRequestDTO {
    private String username;
    private String password;
}
