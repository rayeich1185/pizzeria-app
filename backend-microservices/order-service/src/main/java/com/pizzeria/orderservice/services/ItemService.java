package com.pizzeria.orderservice.services;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.pizzeria.orderservice.utils.dto.ItemDTO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import com.pizzeria.orderservice.entities.Item;
import com.pizzeria.orderservice.repositories.ItemRepository;

import java.io.IOException;
import java.util.Map;

@Service
public class ItemService {
    private static final Logger logger = LoggerFactory.getLogger(ItemService.class);

    private final ItemRepository itemRepository;

    public ItemService(ItemRepository itemRepository) {
        this.itemRepository = itemRepository;
    }

    protected ItemDTO itemToItemDTO(Item item){
        ItemDTO itemDTO = new ItemDTO();

        itemDTO.setItemId(item.getItemId());
        itemDTO.setType(item.getType());
        try {
            ObjectMapper objectMapper = new ObjectMapper();
            itemDTO.setDetails(objectMapper.readValue(item.getDetails(), Map.class));
        } catch (IOException e) {
            e.printStackTrace();
            itemDTO.setDetails(null);
        }
        itemDTO.setPrice(item.getPrice());

        return itemDTO;
    }
}
