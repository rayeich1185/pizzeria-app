package com.pizzeria.orderservice.repositories;

import org.springframework.stereotype.Repository;
import org.springframework.data.jpa.repository.JpaRepository;

import com.pizzeria.orderservice.entities.Order;

@Repository
public interface OrderRepository extends JpaRepository<Order, Long> {
}
