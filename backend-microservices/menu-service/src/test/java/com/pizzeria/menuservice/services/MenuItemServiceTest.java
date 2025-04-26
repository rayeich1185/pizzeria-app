package com.pizzeria.menuservice.services;

import com.pizzeria.menuservice.entities.MenuItem;
import com.pizzeria.menuservice.repositories.MenuItemRepository;
import com.pizzeria.menuservice.utils.enums.ItemCategory;

import com.pizzeria.menuservice.utils.exceptions.MenuItemNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class MenuItemServiceTest {
    @Mock
    private MenuItemRepository menuItemRepository;

    @InjectMocks
    private MenuItemService menuItemService;

    private MenuItem testMenuItem;

    @BeforeEach
    void setup(){
        testMenuItem = new MenuItem();
        testMenuItem.setId(1L);
        testMenuItem.setName("Test Item");
        testMenuItem.setDescription("Test Description");
        testMenuItem.setCategory(ItemCategory.MEAT);
        testMenuItem.setBasePrice(new BigDecimal("10.00"));
        testMenuItem.setImageSvg("Test SVG");
    }

    @Test
    void testGetAllMenuItemsIncludingDeleted() {
        // Given
        List<MenuItem> menuItems = Arrays.asList(new MenuItem(), new MenuItem());
        when(menuItemRepository.findAll()).thenReturn(menuItems);

        // When
        List<MenuItem> result = menuItemService.getAllMenuItemsIncludingDeleted();

        // Then
        assertEquals(menuItems, result);
        verify(menuItemRepository).findAll();
    }

    @Test
    void testGetAllActiveMenuItems() {
        // Given
        List<MenuItem> activeMenuItems = Arrays.asList(new MenuItem(), new MenuItem());
        when(menuItemRepository.findAllByDeletedFalse()).thenReturn(activeMenuItems);

        // When
        List<MenuItem> result = menuItemService.getAllActiveMenuItems();

        // Then
        assertEquals(activeMenuItems, result);
        verify(menuItemRepository).findAllByDeletedFalse();
    }

    @Test
    void testGetMenuItemById() {
        // Given
        Long id = 1L;
        MenuItem menuItem = new MenuItem();
        when(menuItemRepository.findById(id)).thenReturn(Optional.of(menuItem));

        // When
        MenuItem result = menuItemService.getMenuItemById(id);

        // Then
        assertEquals(menuItem, result);
        verify(menuItemRepository).findById(id);
    }

    @Test
    void testGetMenuItemByIdNotFound() {
        // Given
        Long id = 1L;
        when(menuItemRepository.findById(id)).thenReturn(Optional.empty());

        // When and Then
        assertThrows(MenuItemNotFoundException.class, () -> menuItemService.getMenuItemById(id));
        verify(menuItemRepository).findById(id);
    }

    @Test
    void testCreateMenuItem() {
        // Given
        MenuItem menuItem = new MenuItem();
        when(menuItemRepository.save(menuItem)).thenReturn(menuItem);

        // When
        MenuItem result = menuItemService.createMenuItem(menuItem);

        // Then
        assertEquals(menuItem, result);
        verify(menuItemRepository).save(menuItem);
    }

    @Test
    void testUpdateMenuItem_ExistingId_ReturnsUpdatedMenuItem() {
        // Given
        Long id = 1L;
        MenuItem updatedMenuItem = new MenuItem();
        updatedMenuItem.setName("New Name");
        updatedMenuItem.setDescription("Updated Description");
        updatedMenuItem.setBasePrice(new BigDecimal("5.00"));
        updatedMenuItem.setImageSvg("Updated SVG");
        updatedMenuItem.setCategory(ItemCategory.VEGETABLE);

        when(menuItemRepository.findById(id)).thenReturn(Optional.of(testMenuItem));
        when(menuItemRepository.save(any(MenuItem.class))).thenReturn(testMenuItem); // Capture any MenuItem

        // When
        MenuItem result = menuItemService.updateMenuItem(id, updatedMenuItem);

        // Then
        assertNotNull(result);
        assertEquals("New Name", result.getName());
        assertEquals("Updated Description", result.getDescription());
        assertEquals(new BigDecimal("5.00"), result.getBasePrice());
        assertEquals("Updated SVG", result.getImageSvg());
        assertEquals(ItemCategory.VEGETABLE, result.getCategory());
        verify(menuItemRepository, times(1)).findById(id);
        verify(menuItemRepository, times(1)).save(any(MenuItem.class));
    }

    @Test
    void testUpdateMenuItemNotFound() {
        // Given
        Long id = 1L;
        MenuItem updatedMenuItem = new MenuItem();
        when(menuItemRepository.findById(id)).thenReturn(Optional.empty());

        // When and Then
        assertThrows(MenuItemNotFoundException.class, () -> menuItemService.updateMenuItem(id, updatedMenuItem));
        verify(menuItemRepository).findById(id);
    }

    @Test
    void testDeleteMenuItem() {
        // Given
        Long id = 1L;
        when(menuItemRepository.findById(id)).thenReturn(Optional.of(new MenuItem()));

        // When
        menuItemService.deleteMenuItem(id);

        // Then
        verify(menuItemRepository).findById(id);
        verify(menuItemRepository).deleteById(id);
    }

    @Test
    void testDeleteMenuItemNotFound() {
        // Given
        Long id = 1L;
        when(menuItemRepository.findById(id)).thenReturn(Optional.empty());

        // When and Then
        assertThrows(MenuItemNotFoundException.class, () -> menuItemService.deleteMenuItem(id));
        verify(menuItemRepository).findById(id);
    }

    @Test
    void testGetItemPrice() {
        // Given
        Long id = 1L;
        MenuItem menuItem = new MenuItem();
        menuItem.setBasePrice(new BigDecimal("10.00"));
        when(menuItemRepository.findById(id)).thenReturn(Optional.of(menuItem));

        // When
        BigDecimal result = menuItemService.getItemPrice(id);

        // Then
        assertEquals(new BigDecimal("10.00"), result);
        verify(menuItemRepository).findById(id);
    }
}
