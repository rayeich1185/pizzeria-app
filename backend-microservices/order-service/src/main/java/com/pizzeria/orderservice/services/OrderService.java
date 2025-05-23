package com.pizzeria.orderservice.services;

import com.pizzeria.orderservice.utils.enums.OrderStatus;
import jakarta.transaction.Transactional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import com.pizzeria.orderservice.entities.Order;
import com.pizzeria.orderservice.entities.Item;
import com.pizzeria.orderservice.repositories.OrderRepository;
import com.pizzeria.orderservice.utils.dto.*;
import com.pizzeria.orderservice.utils.exceptions.UserNotFoundException;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class OrderService {
    private static final Logger logger = LoggerFactory.getLogger(OrderService.class);

    private final OrderManager orderManager;
    private final OrderRepository orderRepository;
    private final ItemFactory itemFactory;
    private final ItemService itemService;
    private final WebClient webClient;

    @Autowired
    public OrderService(OrderManager orderManager,
                        OrderRepository orderRepository,
                        ItemFactory itemFactory,
                        ItemService itemService,
                        WebClient.Builder webClientBuilder) {
        this.orderManager = orderManager;
        this.orderRepository = orderRepository;
        this.itemFactory = itemFactory;
        this.itemService = itemService;
        this.webClient = webClientBuilder.baseUrl("http://localhost:8080").build();
    }

    @Transactional
    public Order createOrder(Long userId, List<Item> itemList) {
        ApiResponseDTO<UserDTO> apiResponse = webClient.get()
                .uri("/api/users/{userId}", userId)
                .retrieve()
                .bodyToMono(new ParameterizedTypeReference<ApiResponseDTO<UserDTO>>() {
                })
                .block();

        if (apiResponse == null || apiResponse.getData() == null) {
            throw new UserNotFoundException("User not found, or invalid response received");
        }

        UserDTO userDTO = apiResponse.getData();

        Order order = new Order();
        order.setUserId(userDTO.getId());
        order.setOrderTime(LocalDateTime.now());
        order.setOrderStatus(OrderStatus.PENDING);
        order.setItems(itemList);

        return order;
    }

    public List<OrderResponseDTO> findAllOrders() {
        logger.info("Finding all orders");

        List<Order> orderList = orderRepository.findAll();

        return orderList.stream()
                .map(this::orderToOrderResponseDTO)
                .collect(Collectors.toList());
    }

    private OrderResponseDTO orderToOrderResponseDTO(Order order) {
        List<ItemDTO> itemDTOList = order.getItems().stream()
                .map(itemService::itemToItemDTO)
                .collect(Collectors.toList());

        OrderResponseDTO orderResponseDTO = new OrderResponseDTO();
        orderResponseDTO.setOrderId(order.getId());
        orderResponseDTO.setUserId(order.getUserId());
        orderResponseDTO.setItems(itemDTOList);
        orderResponseDTO.setOrderDate(order.getOrderTime());
        orderResponseDTO.setOrderStatus(order.getOrderStatus());
        orderResponseDTO.setTotalAmount(order.getTotalAmount());

        if (order.getDeliveryDetails() != null) {
            orderResponseDTO.setDeliveryDetailsId(order.getDeliveryDetails().getDeliveryDetailsId());
        }
        orderResponseDTO.setPaymentTransactionId(order.getPaymentTransactionId());

        return orderResponseDTO;
    }
}
