package com.pizzeria.orderservice.services;

import com.pizzeria.orderservice.entities.Item;
import com.pizzeria.orderservice.utils.enums.ItemType;
import org .slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import com.pizzeria.orderservice.entities.itemCategories.*;

import java.util.Map;

@Service
public class ItemFactory {
    private static final Logger logger = LoggerFactory.getLogger(ItemFactory.class);

    public Item createItem(ItemType type, Map<String, Object> details){
        return switch (type) {
            case ItemType.PIZZA -> createPizza(details);
            case ItemType.DRINK -> createDrink(details);
            case ItemType.APPETIZER -> createAppetizer(details);
            case ItemType.SAUCE -> createSauce(details);
            case ItemType.DESERT -> createDesert(details);
            default -> throw new IllegalArgumentException("Unknown item type.");
        };
    }

    private Pizza createPizza(Map<String, Object> details){
        return null;
    }

    private Drink createDrink(Map<String, Object> details){
        return null;
    }

    private Appetizer createAppetizer(Map<String, Object> details){
        return null;
    }

    private Sauce createSauce(Map<String, Object> details){
        return null;
    }

    private Desert createDesert(Map<String, Object> details){
        return null;
    }
}
