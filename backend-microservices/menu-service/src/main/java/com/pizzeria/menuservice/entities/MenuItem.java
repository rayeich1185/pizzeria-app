package com.pizzeria.menuservice.entities;

import com.pizzeria.menuservice.utils.enums.ItemCategory;
import jakarta.persistence.*;
import jakarta.validation.constraints.DecimalMin;
import lombok.*;

import java.math.BigDecimal;

@Entity
@Table(name = "menu_item")
@Data
@NoArgsConstructor
@RequiredArgsConstructor
public class MenuItem {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @Column(name = "ingredient_name")
    @NonNull
    private String name;

    @Column(name = "description")
    private String description;

    @Column(name = "base_price", scale = 2, precision = 8)
    @NonNull
    @DecimalMin(value = "0.00", message = "Base price must be greater than or equal to 0.00")
    private BigDecimal basePrice;

    @Column(name = "category")
    @NonNull
    private ItemCategory category;

    @Column(name = "image_svg", columnDefinition = "TEXT")
    private String imageSvg;

    @Column(name = "deleted")
    @NonNull
    private boolean deleted = false;
}