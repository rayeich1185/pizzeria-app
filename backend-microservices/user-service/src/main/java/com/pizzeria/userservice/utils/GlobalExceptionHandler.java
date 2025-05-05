package com.pizzeria.userservice.utils;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import com.pizzeria.userservice.utils.exceptions.*;
import com.pizzeria.userservice.utils.dto.ApiResponseDTO;

@RestControllerAdvice
public class GlobalExceptionHandler {
    private static final Logger logger = LoggerFactory.getLogger(GlobalExceptionHandler.class);

    @ExceptionHandler(UserAlreadyExistsException.class)
    public ResponseEntity<ApiResponseDTO> handleUserAlreadyExistsException(UserAlreadyExistsException ex) {
        logger.error("User already exists: {}", ex.getMessage(), ex);
        return ResponseEntity.status(HttpStatus.CONFLICT)
                .body(new ApiResponseDTO<>(false, null, ex.getMessage()));
    }

    @ExceptionHandler(UserNotFoundException.class)
    public ResponseEntity<ApiResponseDTO> handleUserNotFoundException(UserNotFoundException ex) {
        logger.error("User not found: {}", ex.getMessage(), ex);
        return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(new ApiResponseDTO<>(false, null, ex.getMessage()));
    }

    @ExceptionHandler(UserAddressNotFoundException.class)
    public ResponseEntity<ApiResponseDTO> handleUserAddressNotFoundException(UserAddressNotFoundException ex) {
        logger.error("User address not found: {}", ex.getMessage(), ex);
        return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(new ApiResponseDTO<>(false, null, ex.getMessage()));
    }

    @ExceptionHandler(UserHasNoAddressException.class)
    public ResponseEntity<ApiResponseDTO> handleUserHasNoAddressesException(UserHasNoAddressException ex) {
        logger.error("User has no addresses: {}", ex.getMessage(), ex);
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(new ApiResponseDTO<>(false, null, ex.getMessage()));
    }

    @ExceptionHandler(InvalidCredentialsException.class)
    public ResponseEntity<ApiResponseDTO> handleInvalidCredentialsException(InvalidCredentialsException ex) {
        logger.error("Invalid credentials: {}", ex.getMessage(), ex);
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                .body(new ApiResponseDTO<>(false, null, ex.getMessage()));
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ApiResponseDTO> handleMethodArgumentNotValidException(MethodArgumentNotValidException ex) {
        logger.error("Validation error: {}", ex.getMessage(), ex);
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(new ApiResponseDTO<>(false, null, ex.getMessage()));
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiResponseDTO> handleException(Exception ex) {
        logger.error("Internal server error: {}", ex.getMessage(), ex);
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(new ApiResponseDTO<>(false, null, ex.getMessage()));
    }
}
