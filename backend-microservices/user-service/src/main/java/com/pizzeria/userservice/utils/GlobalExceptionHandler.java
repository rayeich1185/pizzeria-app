package com.pizzeria.userservice.utils; // Ensure correct package

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import com.pizzeria.userservice.utils.exceptions.*;

@RestControllerAdvice
public class GlobalExceptionHandler {
    private static final Logger logger = LoggerFactory.getLogger(GlobalExceptionHandler.class);

    @ExceptionHandler(UserAlreadyExistsException.class)
    public ResponseEntity<Object> handleUserAlreadyExistsException(UserAlreadyExistsException ex) {
        logger.error("User already exists: {}", ex.getMessage(), ex); // Include exception details
        return new ResponseEntity<>(ex.getMessage(), HttpStatus.CONFLICT); // Use HttpStatus.CONFLICT (409)
    }

    @ExceptionHandler(UserNotFoundException.class)
    public ResponseEntity<Object> handleUserNotFoundException(UserNotFoundException ex) {
        logger.error("User not found: {}", ex.getMessage(), ex);
        return new ResponseEntity<>(ex.getMessage(), HttpStatus.NOT_FOUND); // Use HttpStatus.NOT_FOUND (404)
    }

    @ExceptionHandler(UserAddressNotFoundException.class)
    public ResponseEntity<Object> handleUserAddressNotFoundException(UserAddressNotFoundException ex) {
        logger.error("User address not found: {}", ex.getMessage(), ex);
        return new ResponseEntity<>(ex.getMessage(), HttpStatus.NOT_FOUND); // Use HttpStatus.NOT_FOUND (404)
    }

    @ExceptionHandler(InvalidCredentialsException.class)
    public ResponseEntity<Object> handleInvalidCredentialsException(InvalidCredentialsException ex) {
        logger.warn("Invalid credentials: {}", ex.getMessage()); // Use WARN for authentication failures
        return new ResponseEntity<>(ex.getMessage(), HttpStatus.UNAUTHORIZED); // Use HttpStatus.UNAUTHORIZED (401)
    }

    @ExceptionHandler(Exception.class) // Catch-all for other exceptions
    public ResponseEntity<Object> handleGeneralException(Exception ex) {
        logger.error("An unexpected error occurred: {}", ex.getMessage(), ex);
        return new ResponseEntity<>("An unexpected error occurred", HttpStatus.INTERNAL_SERVER_ERROR); // Use HttpStatus.INTERNAL_SERVER_ERROR (500)
    }
}