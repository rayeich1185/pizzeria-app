package com.pizzeria.orderservice.utils.dto;

import lombok.Data;

import java.util.List;

@Data
public class OrderRequestDTO {
    private Long userId;
    private List<ItemRequestDTO> items;
    private Long deliveryDetailsId;
}
