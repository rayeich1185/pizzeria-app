package com.pizzeria.menuservice.repositories;

import com.pizzeria.menuservice.entities.MenuItem;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface MenuItemRepository extends JpaRepository<MenuItem, Long> {
    public List<MenuItem> findAllByDeletedFalse();
}
