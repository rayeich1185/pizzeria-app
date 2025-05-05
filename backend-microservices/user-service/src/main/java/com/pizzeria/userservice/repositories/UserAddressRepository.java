package com.pizzeria.userservice.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.pizzeria.userservice.entities.UserAddress;

@Repository
public interface UserAddressRepository extends JpaRepository<UserAddress, Long> {
}
