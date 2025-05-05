package com.pizzeria.userservice.utils.dto;

import jakarta.validation.constraints.Pattern;
import lombok.Data;

import com.pizzeria.userservice.utils.constraints.PasswordConstraint;

@Data
public class PasswordChangeRequestDTO {
    private long userId;
    private String oldPassword;

    @Pattern(regexp = PasswordConstraint.PASSWORD_PATTERN,
            message = "Password must be at least 8 characters long and contain at least one uppercase letter, " +
                    "one lowercase letter, one number, and one special character")
    private String newPassword;
}
