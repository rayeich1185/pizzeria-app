package com.pizzeria.orderservice.utils.dto;

import lombok.Data;

import java.util.Map;

@Data
public class ItemRequestDTO {
    private String type;
    private Map<String, Object> details;
}
