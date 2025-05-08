package com.pizzeria.orderservice.services;

import com.pizzeria.orderservice.utils.dto.UserDTO;
import com.pizzeria.orderservice.utils.enums.OrderStatus;
import org.springframework.stereotype.Service;

import com.pizzeria.orderservice.entities.Order;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

@Service
public class OrderManager {
    private final Map<Long, Order> orders = new HashMap<>();
    private Long nextOrderId = 1L;

    public Order createOrder(UserDTO userdto){
        Order order = new Order();
        order.setId(nextOrderId++);
        order.setUserId(userdto.getId());
        order.setOrderTime(LocalDateTime.now());
        order.setOrderStatus(OrderStatus.PENDING);
        orders.put(order.getId(), order);
        return order;
    }

    //TODO: Implement exception handling
    public Order getOrder(Long orderId){
        return orders.get(orderId);
    }

    public void updateOrder(Order order){
        orders.put(order.getId(), order);
    }
}
