package com.pizzeria.orderservice.controllers;

import com.pizzeria.orderservice.services.OrderService;
import com.pizzeria.orderservice.utils.dto.ApiResponseDTO;
import com.pizzeria.orderservice.utils.dto.OrderResponseDTO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.http.ResponseEntity;

import com.pizzeria.orderservice.entities.Order;

import java.util.List;

@RestController
@RequestMapping("api/orders")
public class OrderController {
    private static final Logger logger = LoggerFactory.getLogger(OrderController.class);

    private final OrderService orderService;

    @Autowired
    public OrderController(OrderService orderService){
        this.orderService = orderService;
    }

    @GetMapping("/")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public ResponseEntity<ApiResponseDTO<List<OrderResponseDTO>>> getAllOrders(){
        return ResponseEntity.status(HttpStatus.OK)
                .body(new ApiResponseDTO<>(true, orderService.findAllOrders(), "Orders retrieved successfully"));
    }
}
