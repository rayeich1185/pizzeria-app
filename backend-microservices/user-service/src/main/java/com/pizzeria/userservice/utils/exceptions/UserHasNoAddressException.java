package com.pizzeria.userservice.utils.exceptions;

public class UserHasNoAddressException extends RuntimeException {
    public UserHasNoAddressException(String message) {
        super(message);
    }
    public UserHasNoAddressException(String message, Throwable cause) { super(message, cause); }
}
