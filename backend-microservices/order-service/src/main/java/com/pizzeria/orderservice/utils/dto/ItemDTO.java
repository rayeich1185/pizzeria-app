package com.pizzeria.orderservice.utils.dto;

import com.pizzeria.orderservice.utils.enums.ItemType;
import lombok.Data;

import java.util.Map;

@Data
public class ItemDTO {
    private Long itemId;
    private ItemType type;
    private Map<String, Object> details;
    private double price;
}
