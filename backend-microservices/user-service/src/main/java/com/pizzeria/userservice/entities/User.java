package com.pizzeria.userservice.entities;

import lombok.*;
import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import org.hibernate.annotations.CreationTimestamp;

import com.pizzeria.userservice.utils.constraints.PasswordConstraint;
import com.pizzeria.userservice.utils.constraints.PhoneConstraint;
import com.pizzeria.userservice.utils.enums.UserRole;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name="users")
@Data
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@ToString(exclude = {"addresses", "password"})
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @EqualsAndHashCode.Include
    private Long id;

    @Column(unique = true, nullable = false) // Added nullable = false
    @NotBlank
    private String username;

    @Pattern(regexp = PasswordConstraint.PASSWORD_PATTERN,
            message = "Password must be at least 8 characters long and contain at least one uppercase letter, " +
                    "one lowercase letter, one number, and one special character")
    @NotNull
    @Column(nullable = false)  // Added this.
    private String password;

    @NotBlank
    @Column(nullable = false) // Added this.
    private String firstName;

    @NotBlank
    @Column(nullable = false) // Added this.
    private String lastName;

    @Column(unique = true, nullable = false) // Added nullable = false
    @Email
    @NotNull
    private String email;

    @Column(unique = true, nullable = false) // Added nullable = false
    @Pattern(regexp = PhoneConstraint.PHONE_PATTERN, message = "Phone number must be 10 digits")
    @NotNull
    private String phone;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false) // Added this.
    private UserRole userRole;

    @Column(nullable = false)  // Added this.
    private boolean lockedAccount = false;

    @CreationTimestamp
    @Column(nullable = false)  // Added this.
    private LocalDateTime createdAt;

    private LocalDateTime lastOrderSubmitted;
    private LocalDateTime lastLogin;

    @ManyToMany(cascade = {CascadeType.PERSIST, CascadeType.MERGE})
    @JoinTable(name = "users_addresses",
            joinColumns = @JoinColumn(name = "user_id"),
            inverseJoinColumns = @JoinColumn(name = "address_id"))
    private Set<UserAddress> addresses = new HashSet<>();

    @ManyToOne
    @JoinColumn(name = "default_address_id")
    private UserAddress defaultAddress;

    public User(String username, String password, String firstName, String lastName, String email, String phone, UserRole userRole) {
        this.username = username;
        this.password = password;
        this.firstName = firstName;
        this.lastName = lastName;
        this.email = email;
        this.phone = phone;
        this.createdAt = LocalDateTime.now();
        this.userRole = userRole;
    }

    public User() {
        this.createdAt = LocalDateTime.now();
        this.userRole = UserRole.CUSTOMER;
    }

    public void addAddress(UserAddress address) {
        addresses.add(address);
        address.getUsers().add(this);
    }

    public void removeAddress(UserAddress address) {
        addresses.remove(address);
        address.getUsers().remove(this);
    }
}
