package com.pizzeria.userservice.utils.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.Data;

import com.pizzeria.userservice.utils.constraints.PhoneConstraint;

@Data
public class UserUpdateDTO {
    @NotBlank(message = "First name is required")
    private String firstName;

    @NotBlank(message = "Last name is required")
    private String lastName;

    @NotBlank(message = "Email is required")
    @Email(message = "Email must be valid")
    private String email;

    @NotBlank(message = "Phone number is required")
    @Pattern(regexp = PhoneConstraint.PHONE_PATTERN, message = "Phone number must be 10 digits")
    private String phone;
}
