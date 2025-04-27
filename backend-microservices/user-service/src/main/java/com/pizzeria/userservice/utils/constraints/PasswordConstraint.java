package com.pizzeria.userservice.utils.constraints;

public final class PasswordConstraint {
    public static final String PASSWORD_PATTERN = "^(?=.*[0-9])(?=.*[a-z])(?=.*[A-Z])(?=\\S+$).{8,}$";
}
