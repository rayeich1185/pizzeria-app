package com.pizzeria.orderservice.utils.dto;

import lombok.Data;

import com.pizzeria.orderservice.utils.enums.OrderStatus;

import java.time.LocalDateTime;
import java.util.List;

@Data
public class OrderResponseDTO {
    private Long orderId;
    private Long userId;
    private LocalDateTime orderDate;
    private OrderStatus orderStatus;
    private List<ItemDTO> items;
    private double totalAmount;
    private Long deliveryDetailsId;
    private String paymentTransactionId;
}
