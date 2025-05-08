package com.pizzeria.orderservice.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.pizzeria.orderservice.entities.Item;

@Repository
public interface ItemRepository extends JpaRepository<Item, Long> {
}
