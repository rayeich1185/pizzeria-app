package com.pizzeria.orderservice.entities;

import jakarta.persistence.*;
import lombok.Data;

import com.pizzeria.orderservice.utils.enums.OrderStatus;

import java.time.LocalDateTime;
import java.util.List;

@Entity
@Table(name = "orders")
@Data
public class Order {
    @Id
    @GeneratedValue(strategy = jakarta.persistence.GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private Long userId;

    @Column(nullable = false)
    private LocalDateTime orderTime;

    @Column(nullable = false)
    private OrderStatus orderStatus;

    @OneToMany(mappedBy = "order", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Item> items;

    @Column(nullable = false)
    private double totalAmount;

    @OneToOne(cascade = CascadeType.ALL, orphanRemoval = true)
    @JoinColumn(name = "delivery_details_id")
    private DeliveryDetails deliveryDetails;

    private String paymentTransactionId;
}
