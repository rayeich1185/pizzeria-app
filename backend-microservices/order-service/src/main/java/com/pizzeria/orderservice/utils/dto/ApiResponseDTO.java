package com.pizzeria.orderservice.utils.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor
@Data
public class ApiResponseDTO<T> {
    private boolean success;
    private T data;
    private String message;
}
