package com.pizzeria.menuservice.controllers;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.pizzeria.menuservice.entities.MenuItem;
import com.pizzeria.menuservice.repositories.MenuItemRepository;
import com.pizzeria.menuservice.utils.enums.ItemCategory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.math.BigDecimal;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;


@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class MenuItemControllerIntegrationTest {
    @LocalServerPort
    private int port;

    @Autowired
    private TestRestTemplate restTemplate;

    @Autowired
    private MenuItemRepository menuItemRepository;

    @Autowired
    private ObjectMapper objectMapper;

    private String baseUrl;

    @BeforeEach
    void setUp(){
        baseUrl = "http://localhost:" + port + "/api/menu/items";

        menuItemRepository.deleteAll();

        MenuItem item1 = new MenuItem();
        item1.setName("Item 1");
        item1.setCategory(ItemCategory.MEAT);
        item1.setBasePrice(new BigDecimal("10.00"));
        menuItemRepository.save(item1);

        MenuItem item2 = new MenuItem();
        item2.setName("Item 2");
        item2.setCategory(ItemCategory.VEGETABLE);
        item2.setBasePrice(new BigDecimal("5.00"));
        menuItemRepository.save(item2);
    }

    @Test
    void getAllMenuItems_ReturnsAllMenuItems() {
        ResponseEntity<List> response = restTemplate.getForEntity(baseUrl, List.class);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(2, response.getBody().size());
    }
}
