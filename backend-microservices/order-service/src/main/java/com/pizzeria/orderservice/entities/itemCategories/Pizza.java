package com.pizzeria.orderservice.entities.itemCategories;

import com.pizzeria.orderservice.entities.Item;
import com.pizzeria.orderservice.utils.enums.PizzaSize;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Table;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

@Entity
@Table(name = "pizzas")
@Data
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
public class Pizza extends Item {
    @Enumerated(EnumType.STRING)
    private PizzaSize size;


}
