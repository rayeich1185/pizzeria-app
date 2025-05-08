package com.pizzeria.orderservice.entities;

import lombok.Data;
import jakarta.persistence.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "delivery_details")
@Data

public class DeliveryDetails {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long deliveryDetailsId;

    @Column(nullable = false)
    private String deliveryAddress;

    private String preferredDeliveryTime;

    @Column(nullable = false)
    private Long deliveryDriverId;

    private LocalDateTime actualDeliveryTime;
}
