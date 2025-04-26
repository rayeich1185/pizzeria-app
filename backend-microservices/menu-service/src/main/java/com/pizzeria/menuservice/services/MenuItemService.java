package com.pizzeria.menuservice.services;

import com.pizzeria.menuservice.entities.MenuItem;
import com.pizzeria.menuservice.repositories.MenuItemRepository;
import com.pizzeria.menuservice.utils.exceptions.MenuItemNotFoundException;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;

@Service
public class MenuItemService {
    private final MenuItemRepository menuItemRepository;
    private static final Logger logger = LoggerFactory.getLogger(MenuItemService.class);

    @Autowired
    public MenuItemService(MenuItemRepository menuItemRepository) {
        this.menuItemRepository = menuItemRepository;
    }

    public List<MenuItem> getAllMenuItemsIncludingDeleted() {
        logger.info("Getting all menu items including deleted");
        return menuItemRepository.findAll();
    }

    public List<MenuItem> getAllActiveMenuItems(){
        logger.info("Getting all active/current menu items");
        return menuItemRepository.findAllByDeletedFalse();
    }

    public MenuItem getMenuItemById(Long id) {
        logger.info("Getting menu item by id: {}", id);
        return menuItemRepository.findById(id).orElseThrow(() -> {
            logger.error("Menu item not found with id: {}", id);
            return new MenuItemNotFoundException("Menu item not found");
        });
    }

    public MenuItem createMenuItem(@Valid MenuItem menuItem) {
        logger.info("Creating menu item: {}", menuItem.getName());
        MenuItem savedMenuItem = menuItemRepository.save(menuItem);
        logger.info("Menu item created: {}", savedMenuItem.getName());
        return savedMenuItem;
    }

    public MenuItem updateMenuItem(Long id, @Valid MenuItem menuItem) {
        logger.info("Updating menu item with id: {}", id);
        MenuItem existingMenuItem = menuItemRepository.findById(id).orElseThrow(() -> {
            logger.error("Menu item not found with id: {}", id);
            return new MenuItemNotFoundException("Menu item not found");
        });

        existingMenuItem.setName(menuItem.getName());
        existingMenuItem.setDescription(menuItem.getDescription());
        existingMenuItem.setCategory(menuItem.getCategory());
        existingMenuItem.setBasePrice(menuItem.getBasePrice());
        existingMenuItem.setImageSvg(menuItem.getImageSvg());

        MenuItem updatedMenuItem = menuItemRepository.save(existingMenuItem);
        logger.info("Menu item updated: {}", updatedMenuItem.getName());
        return updatedMenuItem;
    }

    public void deleteMenuItem(Long id) {
        logger.info("Deleting menu item with ID: {}", id); // Add this line
        MenuItem existingMenuItem = menuItemRepository.findById(id).orElseThrow(() -> {
            logger.error("Menu item with ID {} not found", id); // Add this line
            return new MenuItemNotFoundException("Menu item not found");
        });
        existingMenuItem.setDeleted(true);
        menuItemRepository.save(existingMenuItem);
        logger.info("Menu item with ID {} deleted successfully", id); // Add this line
    }

    public BigDecimal getItemPrice(Long id) {
        logger.info("Getting price for menu item with ID: {}", id); // Add this line
        MenuItem menuItem = menuItemRepository.findById(id).orElseThrow(() -> {
            logger.error("Menu item with ID {} not found", id); // Add this line
            return new MenuItemNotFoundException("Menu item not found");
        });
        return menuItem.getBasePrice();
    }
}
