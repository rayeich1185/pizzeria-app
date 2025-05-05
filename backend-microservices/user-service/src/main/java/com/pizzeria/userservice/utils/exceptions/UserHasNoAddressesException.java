package com.pizzeria.userservice.utils.exceptions;

public class UserHasNoAddressesException extends RuntimeException {
    public UserHasNoAddressesException(String message) {
        super(message);
    }
    public UserHasNoAddressesException(String message, Throwable cause) {super(message, cause); }
}
