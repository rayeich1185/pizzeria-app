package com.pizzeria.userservice.utils.exceptions;

public class UserAddressNotFoundException extends RuntimeException {
    public UserAddressNotFoundException(String message) {
        super(message);
    }
    public UserAddressNotFoundException(String message, Throwable cause) { super(message, cause); }
}
