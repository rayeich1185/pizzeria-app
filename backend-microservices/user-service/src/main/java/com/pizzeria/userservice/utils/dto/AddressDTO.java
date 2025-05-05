package com.pizzeria.userservice.utils.dto;

import lombok.Data;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;

import com.pizzeria.userservice.utils.constraints.ZipCodeConstraint;

@Data
public class AddressDTO {
    private Long id; // Include ID for updates
    @NotBlank(message = "Street address is required")
    private String streetAddress;

    private String unitNumber;

    @NotBlank(message = "City is required")
    private String city;

    @NotBlank(message = "State is required")
    private String state;

    @NotBlank(message = "Zip code is required")
    @Pattern(regexp = ZipCodeConstraint.ZIP_CODE_PATTERN, message = "Zip code must be 5 digits")
    private String zipCode;
}
