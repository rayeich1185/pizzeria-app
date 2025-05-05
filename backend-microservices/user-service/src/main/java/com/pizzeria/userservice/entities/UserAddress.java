package com.pizzeria.userservice.entities;

import lombok.*;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;

import com.pizzeria.userservice.utils.constraints.ZipCodeConstraint;

import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name="addresses")
@Data
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@ToString(exclude = {"users"})
public class UserAddress {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @EqualsAndHashCode.Include
    private Long id;

    @NotBlank(message = "Street address is required")
    @Column(name = "street_address", nullable = false)
    private String streetAddress;

    @Column(name = "unit_number")
    private String unitNumber;

    @NotBlank(message = "City is required")
    @Column(name = "city", nullable = false)
    private String city;

    @NotBlank(message = "State is required")
    @Column(name = "state", nullable = false)
    private String state;

    @NotBlank(message = "Zip code is required")
    @Pattern(regexp = ZipCodeConstraint.ZIP_CODE_PATTERN, message = "Zip code must be 5 digits")
    @Column(name = "zip_code", nullable = false)
    private String zipCode;

    @ManyToMany(mappedBy = "addresses")
    private Set<User> users = new HashSet<>();

    public UserAddress(String streetAddress, String city, String state, String zipCode) {
        this.streetAddress = streetAddress;
        this.city = city;
        this.state = state;
        this.zipCode = zipCode;
    }
}
